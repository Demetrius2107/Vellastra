package com.demetrius.vellastra.mail.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.mail.config.MailProperties;
import com.demetrius.vellastra.mail.infrastructure.mapper.EmailSendLogMapper;
import com.demetrius.vellastra.mail.infrastructure.po.EmailSendLogPO;
import com.demetrius.vellastra.mail.infrastructure.po.MailTemplatePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: SendEngineService</p>
 * <p>Description: 邮件发送引擎，支持异步队列、批量发送、限速、状态机与退信处理</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Service
public class SendEngineService {

    private final EmailSendLogMapper sendLogMapper;
    private final MailSenderService mailSenderService;
    private final MailTemplateService templateService;
    private final MailTemplateRenderer renderer;
    private final MailProperties mailProperties;

    /** 发送速率控制（每秒最多 N 封） */
    private final Semaphore rateLimiter;

    public SendEngineService(EmailSendLogMapper sendLogMapper,
                             MailSenderService mailSenderService,
                             MailTemplateService templateService,
                             MailTemplateRenderer renderer,
                             MailProperties mailProperties) {
        this.sendLogMapper = sendLogMapper;
        this.mailSenderService = mailSenderService;
        this.templateService = templateService;
        this.renderer = renderer;
        this.mailProperties = mailProperties;
        this.rateLimiter = new Semaphore(mailProperties.getSendRateLimitPerSecond());
    }

    // ===================== 单封发送 =====================

    /** 使用模板发送单封邮件（创建发送记录，异步执行） */
    public Long sendWithTemplate(String to, String templateCode, Map<String, Object> variables) {
        MailTemplatePO template = templateService.getByCode(templateCode);
        if (template == null) throw new RuntimeException("模板不存在: " + templateCode);

        String subject = renderer.render(template.getSubject(), variables);
        String content = renderer.render(template.getContent(), variables);

        EmailSendLogPO logPO = createLog(to, subject, templateCode, null);
        asyncSend(logPO, content);
        return logPO.getId();
    }

    // ===================== 批量发送 =====================

    /** 批量发送：给多个订阅者发同一模板，返回批次号 */
    public String sendBatch(List<String> emails, String templateCode, Map<String, Object> baseVariables) {
        MailTemplatePO template = templateService.getByCode(templateCode);
        if (template == null) throw new RuntimeException("模板不存在: " + templateCode);

        String batchNo = "B" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        log.info("批量发送启动: batch={}, recipients={}, template={}", batchNo, emails.size(), templateCode);

        for (String email : emails) {
            String subject = renderer.render(template.getSubject(), baseVariables);
            String content = renderer.render(template.getContent(), baseVariables);
            EmailSendLogPO logPO = createLog(email, subject, templateCode, batchNo);
            asyncSend(logPO, content);
        }
        return batchNo;
    }

    // ===================== 重试 & 状态机 =====================

    /** 状态机流转: pending → sending → sent / failed / bounced */
    @Async
    public CompletableFuture<Void> asyncSend(EmailSendLogPO logPO, String content) {
        updateStatus(logPO, "sending");

        // 限速：获取信号量，等待直到允许发送
        try {
            boolean acquired = rateLimiter.tryAcquire(5, TimeUnit.SECONDS);
            if (!acquired) {
                failLog(logPO, "发送速率限制，等待超时");
                return CompletableFuture.completedFuture(null);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failLog(logPO, "发送中断");
            return CompletableFuture.completedFuture(null);
        }

        boolean success = mailSenderService.sendHtml(logPO.getTo(), logPO.getSubject(), content);
        rateLimiter.release();

        if (success) {
            updateStatus(logPO, "sent");
        } else {
            handleFailure(logPO, "SMTP 发送失败");
        }
        return CompletableFuture.completedFuture(null);
    }

    /** 重试失败的邮件 */
    @Transactional
    public void retry(Long sendLogId) {
        EmailSendLogPO logPO = sendLogMapper.selectById(sendLogId);
        if (logPO == null) return;
        logPO.setRetryCount(0);
        logPO.setStatus("pending");
        logPO.setErrorMsg(null);
        logPO.setUpdateTime(LocalDateTime.now());
        sendLogMapper.updateById(logPO);
        log.info("邮件已重新加入队列: id={}, to={}", sendLogId, logPO.getTo());
    }

    /** 定时重试失败的邮件（最多重试 3 次） */
    @Scheduled(cron = "0 */5 * * * ?")
    public void autoRetryFailed() {
        List<EmailSendLogPO> failed = sendLogMapper.selectList(
                new LambdaQueryWrapper<EmailSendLogPO>()
                        .eq(EmailSendLogPO::getStatus, "failed")
                        .lt(EmailSendLogPO::getRetryCount, 3));
        if (failed.isEmpty()) return;
        log.info("自动重试失败邮件: count={}", failed.size());
        for (EmailSendLogPO logPO : failed) {
            logPO.setRetryCount(logPO.getRetryCount() + 1);
            logPO.setStatus("pending");
            logPO.setUpdateTime(LocalDateTime.now());
            sendLogMapper.updateById(logPO);
        }
    }

    // ===================== 退信处理 =====================

    /** 处理退信（SMTP 返回永久失败时调用） */
    @Transactional
    public void markBounced(Long sendLogId, String bounceReason) {
        EmailSendLogPO logPO = sendLogMapper.selectById(sendLogId);
        if (logPO == null) return;
        logPO.setStatus("bounced");
        logPO.setBounceReason(bounceReason);
        logPO.setUpdateTime(LocalDateTime.now());
        sendLogMapper.updateById(logPO);
        log.warn("邮件退信: id={}, to={}, reason={}", sendLogId, logPO.getTo(), bounceReason);
    }

    // ===================== 查询 =====================

    public IPage<EmailSendLogPO> listLogs(int current, int size, String status, String batchNo) {
        return sendLogMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<EmailSendLogPO>()
                        .eq(status != null, EmailSendLogPO::getStatus, status)
                        .eq(batchNo != null, EmailSendLogPO::getBatchNo, batchNo)
                        .orderByDesc(EmailSendLogPO::getCreateTime));
    }

    // ===================== 内部 =====================

    private EmailSendLogPO createLog(String to, String subject, String templateCode, String batchNo) {
        EmailSendLogPO po = new EmailSendLogPO();
        po.setTo(to); po.setSubject(subject); po.setTemplateCode(templateCode);
        po.setBatchNo(batchNo); po.setStatus("pending");
        po.setRetryCount(0);
        po.setCreateTime(LocalDateTime.now()); po.setUpdateTime(LocalDateTime.now());
        sendLogMapper.insert(po);
        return po;
    }

    private void updateStatus(EmailSendLogPO po, String status) {
        po.setStatus(status);
        po.setUpdateTime(LocalDateTime.now());
        sendLogMapper.updateById(po);
    }

    private void failLog(EmailSendLogPO po, String reason) {
        po.setStatus("failed");
        po.setErrorMsg(reason);
        po.setUpdateTime(LocalDateTime.now());
        sendLogMapper.updateById(po);
        log.warn("邮件发送失败: id={}, to={}, reason={}", po.getId(), po.getTo(), reason);
    }

    private void handleFailure(EmailSendLogPO po, String reason) {
        po.setStatus("failed");
        po.setErrorMsg(reason);
        po.setUpdateTime(LocalDateTime.now());
        sendLogMapper.updateById(po);
        log.warn("邮件发送失败: id={}, to={}, reason={}", po.getId(), po.getTo(), reason);
    }
}
