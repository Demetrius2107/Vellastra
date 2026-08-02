package com.demetrius.vellastra.mail.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.mail.config.MailProperties;
import com.demetrius.vellastra.mail.infrastructure.mapper.SubscriberMapper;
import com.demetrius.vellastra.mail.infrastructure.po.SubscriberPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SubscriberService {

    private final SubscriberMapper subscriberMapper;
    private final MailTemplateRenderer templateRenderer;
    private final MailSenderService mailSenderService;
    private final MailProperties mailProperties;

    public SubscriberService(SubscriberMapper subscriberMapper,
                             MailTemplateRenderer templateRenderer,
                             MailSenderService mailSenderService,
                             MailProperties mailProperties) {
        this.subscriberMapper = subscriberMapper;
        this.templateRenderer = templateRenderer;
        this.mailSenderService = mailSenderService;
        this.mailProperties = mailProperties;
    }

    /** 订阅（double opt-in：先发确认邮件） */
    @Transactional
    public Long subscribe(String email, String name) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            throw new RuntimeException("邮箱格式不正确");
        }
        // 检查是否已存在
        SubscriberPO existing = subscriberMapper.selectOne(
                new LambdaQueryWrapper<SubscriberPO>().eq(SubscriberPO::getEmail, email));
        if (existing != null) {
            if ("confirmed".equals(existing.getStatus())) {
                throw new RuntimeException("该邮箱已订阅");
            }
            // 重新发送确认邮件
            sendConfirmEmail(existing);
            return existing.getId();
        }

        SubscriberPO po = new SubscriberPO();
        po.setEmail(email);
        po.setName(name);
        po.setStatus("pending");
        po.setConfirmToken(UUID.randomUUID().toString().replace("-", ""));
        po.setUnsubscribeToken(UUID.randomUUID().toString().replace("-", ""));
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        subscriberMapper.insert(po);
        sendConfirmEmail(po);
        log.info("新订阅（待确认）: email={}, id={}", email, po.getId());
        return po.getId();
    }

    /** 确认订阅（点击邮件中的确认链接） */
    @Transactional
    public boolean confirm(String token) {
        SubscriberPO po = subscriberMapper.selectOne(
                new LambdaQueryWrapper<SubscriberPO>().eq(SubscriberPO::getConfirmToken, token));
        if (po == null) return false;
        po.setStatus("confirmed");
        po.setConfirmedAt(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        subscriberMapper.updateById(po);
        log.info("订阅确认成功: email={}", po.getEmail());
        return true;
    }

    /** 退订（点击邮件中的退订链接） */
    @Transactional
    public boolean unsubscribe(String token) {
        SubscriberPO po = subscriberMapper.selectOne(
                new LambdaQueryWrapper<SubscriberPO>().eq(SubscriberPO::getUnsubscribeToken, token));
        if (po == null) return false;
        po.setStatus("unsubscribed");
        po.setUnsubscribedAt(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        subscriberMapper.updateById(po);
        log.info("订阅退订: email={}", po.getEmail());
        return true;
    }

    /** 分页查询订阅者列表 */
    public IPage<SubscriberPO> list(int current, int size, String status) {
        return subscriberMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<SubscriberPO>()
                        .eq(status != null, SubscriberPO::getStatus, status)
                        .orderByDesc(SubscriberPO::getCreateTime));
    }

    /** 获取所有已确认订阅者邮箱（用于批量发送） */
    public List<String> listConfirmedEmails() {
        return subscriberMapper.selectList(
                        new LambdaQueryWrapper<SubscriberPO>().eq(SubscriberPO::getStatus, "confirmed"))
                .stream().map(SubscriberPO::getEmail).toList();
    }

    /** 统计订阅者数量 */
    public long countConfirmed() {
        return subscriberMapper.selectCount(
                new LambdaQueryWrapper<SubscriberPO>().eq(SubscriberPO::getStatus, "confirmed"));
    }

    // ===================== 内部 =====================

    private void sendConfirmEmail(SubscriberPO po) {
        String confirmLink = templateRenderer.buildConfirmLink(mailProperties.getBaseUrl(), po.getConfirmToken());
        String html = "<h3>确认你的订阅</h3><p>感谢你订阅我们的内容。</p>"
                + "<p><a href=\"" + confirmLink + "\">点击这里确认订阅</a></p>"
                + "<p>如果你没有订阅过，请忽略此邮件。</p>";
        mailSenderService.sendHtml(po.getEmail(), "确认订阅 - " + mailProperties.getFromName(), html);
        log.info("确认邮件已发送: email={}", po.getEmail());
    }
}
