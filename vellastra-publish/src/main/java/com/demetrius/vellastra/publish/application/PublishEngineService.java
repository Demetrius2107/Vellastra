package com.demetrius.vellastra.publish.application;

import com.demetrius.vellastra.publish.infrastructure.persistence.mapper.PublishBuildLogMapper;
import com.demetrius.vellastra.publish.infrastructure.persistence.mapper.PublishTaskMapper;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishBuildLogPO;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishTaskPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PublishEngineService {

    private final PublishTaskMapper publishTaskMapper;
    private final PublishBuildLogMapper publishBuildLogMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${publish.webhook.url:}")
    private String webhookUrl;

    @Value("${publish.build.max-retries:3}")
    private int maxRetries;

    public PublishEngineService(PublishTaskMapper publishTaskMapper,
                                PublishBuildLogMapper publishBuildLogMapper) {
        this.publishTaskMapper = publishTaskMapper;
        this.publishBuildLogMapper = publishBuildLogMapper;
    }

    // ===================== 任务管理 =====================

    /** 创建并加入构建队列 */
    @Transactional
    public Long createTask(Long articleId, String articleTitle, String action, String triggeredBy) {
        PublishTaskPO task = new PublishTaskPO();
        task.setArticleId(articleId);
        task.setArticleTitle(articleTitle);
        task.setAction(action);
        task.setStatus("queued");
        task.setRetryCount(0);
        task.setMaxRetries(maxRetries);
        task.setTriggeredBy(triggeredBy);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        publishTaskMapper.insert(task);
        log.info("构建任务已创建: id={}, articleId={}, action={}", task.getId(), articleId, action);
        return task.getId();
    }

    /** 异步执行构建任务 */
    @Async
    public CompletableFuture<Void> executeBuild(Long taskId) {
        PublishTaskPO task = publishTaskMapper.selectById(taskId);
        if (task == null) return CompletableFuture.completedFuture(null);

        try {
            updateStatus(task, "building", "preparing");
            appendLog(taskId, "preparing", "INFO", "构建任务开始");

            // Stage 1: 准备
            appendLog(taskId, "preparing", "INFO", "正在准备构建环境...");
            Thread.sleep(500);

            // Stage 2: Git 拉取（模拟）
            updateStatus(task, "building", "git-clone");
            appendLog(taskId, "git-clone", "INFO", "正在拉取最新代码...");
            Thread.sleep(500);

            // Stage 3: 构建
            updateStatus(task, "building", "build");
            appendLog(taskId, "build", "INFO", "正在执行构建...");
            Thread.sleep(500);

            // Stage 4: 部署
            updateStatus(task, "building", "deploy");
            appendLog(taskId, "deploy", "INFO", "正在触发 webhook 部署...");
            boolean deployed = triggerWebhook(task);
            appendLog(taskId, "deploy", deployed ? "INFO" : "ERROR",
                    deployed ? "webhook 部署成功" : "webhook 部署失败");

            // Stage 5: 完成
            if (deployed) {
                task.setStatus("success");
                task.setCompletedAt(LocalDateTime.now());
                appendLog(taskId, "complete", "INFO", "构建发布完成");
            } else {
                handleFailure(task, "webhook 部署失败");
            }

        } catch (Exception e) {
            log.error("构建执行异常: taskId={}, error={}", taskId, e.getMessage());
            handleFailure(task, e.getMessage());
        }

        task.setUpdateTime(LocalDateTime.now());
        publishTaskMapper.updateById(task);
        return CompletableFuture.completedFuture(null);
    }

    /** 重试失败任务 */
    @Transactional
    public void retry(Long taskId) {
        PublishTaskPO task = publishTaskMapper.selectById(taskId);
        if (task == null) return;
        task.setRetryCount(0);
        task.setStatus("queued");
        task.setResultLog(null);
        task.setUpdateTime(LocalDateTime.now());
        publishTaskMapper.updateById(task);
        appendLog(taskId, "retry", "INFO", "任务已重新加入队列");
        executeBuild(taskId);
    }

    // ===================== 查询 =====================

    public IPage<PublishTaskPO> listTasks(int current, int size, String status) {
        LambdaQueryWrapper<PublishTaskPO> wrapper = new LambdaQueryWrapper<PublishTaskPO>()
                .orderByDesc(PublishTaskPO::getCreateTime);
        if (status != null) wrapper.eq(PublishTaskPO::getStatus, status);
        return publishTaskMapper.selectPage(new Page<>(current, size), wrapper);
    }

    public List<PublishBuildLogPO> getLogs(Long taskId) {
        return publishBuildLogMapper.selectList(
                new LambdaQueryWrapper<PublishBuildLogPO>()
                        .eq(PublishBuildLogPO::getTaskId, taskId)
                        .orderByAsc(PublishBuildLogPO::getCreateTime));
    }

    // ===================== 内部方法 =====================

    private void updateStatus(PublishTaskPO task, String status, String stage) {
        task.setStatus(status);
        task.setCurrentStage(stage);
        task.setStartedAt(status.equals("building") && task.getStartedAt() == null ? LocalDateTime.now() : task.getStartedAt());
        publishTaskMapper.updateById(task);
    }

    private void appendLog(Long taskId, String stage, String level, String message) {
        PublishBuildLogPO logPO = new PublishBuildLogPO();
        logPO.setTaskId(taskId);
        logPO.setStage(stage);
        logPO.setLevel(level);
        logPO.setMessage(message);
        logPO.setCreateTime(LocalDateTime.now());
        publishBuildLogMapper.insert(logPO);
        // WebSocket 实时推送
        com.demetrius.vellastra.publish.config.PublishWebSocket.pushLog(taskId, stage, level, message);
        log.info("[{}] [{}] {}", taskId, stage, message);
    }

    private boolean triggerWebhook(PublishTaskPO task) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            appendLog(task.getId(), "deploy", "WARN", "未配置 webhook URL，跳过部署");
            return true;
        }
        try {
            String body = String.format(
                    "{\"taskId\":%d,\"articleId\":%d,\"action\":\"%s\"}",
                    task.getId(), task.getArticleId(), task.getAction());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            appendLog(task.getId(), "deploy", "INFO",
                    "HTTP " + response.statusCode() + ": " + truncate(response.body(), 200));
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            appendLog(task.getId(), "deploy", "ERROR", "webhook 调用异常: " + e.getMessage());
            return false;
        }
    }

    private void handleFailure(PublishTaskPO task, String reason) {
        int retryCount = task.getRetryCount() != null ? task.getRetryCount() : 0;
        if (retryCount < maxRetries) {
            task.setRetryCount(retryCount + 1);
            task.setStatus("queued");
            appendLog(task.getId(), "retry", "WARN",
                    String.format("构建失败(%s)，剩余重试次数: %d", reason, maxRetries - retryCount));
        } else {
            task.setStatus("failed");
            task.setResultLog(reason);
            task.setCompletedAt(LocalDateTime.now());
            appendLog(task.getId(), "complete", "ERROR", "构建失败，已耗尽重试次数: " + reason);
        }
    }

    private String truncate(String str, int max) {
        if (str == null) return null;
        return str.length() > max ? str.substring(0, max) : str;
    }
}
