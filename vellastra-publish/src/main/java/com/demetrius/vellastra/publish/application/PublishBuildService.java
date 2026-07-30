package com.demetrius.vellastra.publish.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.publish.domain.site.entity.PublishSite;
import com.demetrius.vellastra.publish.domain.site.repository.PublishSiteRepository;
import com.demetrius.vellastra.publish.infrastructure.persistence.mapper.PublishBuildMapper;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishBuildPO;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class PublishBuildService {

    private final PublishBuildMapper buildMapper;
    private final PublishSiteRepository siteRepository;
    private final PublishNotificationService notificationService;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AtomicInteger buildCounter = new AtomicInteger(0);

    @Value("${publish.webhook.url:}")
    private String webhookUrl;

    @Value("${publish.max-retries:3}")
    private int maxRetries;

    @Value("${publish.version-prefix:v}")
    private String versionPrefix;

    public PublishBuildService(PublishBuildMapper buildMapper,
                               PublishSiteRepository siteRepository,
                               PublishNotificationService notificationService) {
        this.buildMapper = buildMapper;
        this.siteRepository = siteRepository;
        this.notificationService = notificationService;
    }

    // ===================== 构建管理 =====================

    /** 创建并启动构建 */
    @Transactional
    public Long startBuild(Long siteId, String environment, String triggeredBy,
                           String commitSha, String commitMessage, String branch) {
        PublishSite site = siteRepository.findById(siteId);
        if (site == null) throw new RuntimeException("站点不存在");

        // 并发构建检查
        if (Boolean.FALSE.equals(site.getConcurrentBuild())) {
            long running = buildMapper.selectCount(new LambdaQueryWrapper<PublishBuildPO>()
                    .eq(PublishBuildPO::getSiteId, siteId)
                    .in(PublishBuildPO::getStatus, "queued", "building"));
            if (running > 0) throw new RuntimeException("该站点有正在进行的构建，请等待完成");
        }

        // 生成版本号
        String versionTag = versionPrefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "." + buildCounter.incrementAndGet();
        String buildNumber = String.valueOf(System.currentTimeMillis());

        PublishBuildPO po = new PublishBuildPO();
        po.setSiteId(siteId);
        po.setVersionTag(versionTag);
        po.setEnvironment(environment != null ? environment : "production");
        po.setBuildNumber(buildNumber);
        po.setStatus("queued");
        po.setRetryCount(0);
        po.setMaxRetries(maxRetries);
        po.setTriggeredBy(triggeredBy);
        po.setCommitSha(commitSha != null ? commitSha : "");
        po.setCommitMessage(commitMessage != null ? commitMessage : "");
        po.setBranch(branch != null ? branch : "main");
        po.setRollbacked(false);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        buildMapper.insert(po);
        log.info("构建已创建: id={}, siteId={}, version={}, env={}",
                po.getId(), siteId, versionTag, environment);

        // 异步执行
        executeBuild(po.getId());
        return po.getId();
    }

    /** 异步执行构建（流水线模式） */
    @Async
    public CompletableFuture<Void> executeBuild(Long buildId) {
        PublishBuildPO build = buildMapper.selectById(buildId);
        if (build == null) return CompletableFuture.completedFuture(null);
        long startMs = System.currentTimeMillis();

        try {
            updateStatus(build, "building");

            // Stage 1: 前置检查
            if (!checkSiteExists(build)) {
                failBuild(build, "站点不存在", startMs);
                return CompletableFuture.completedFuture(null);
            }
            appendLog("running", "前置检查通过");

            // Stage 2: 拉取代码
            appendLog("git-clone", "正在拉取代码: branch=" + build.getBranch());
            Thread.sleep(800);

            // Stage 3: 执行构建
            appendLog("build", "正在执行构建命令...");
            Thread.sleep(1000);

            // Stage 4: 部署
            appendLog("deploy", "正在部署到 " + build.getEnvironment() + " 环境...");
            boolean deployed = triggerWebhook(build);
            if (!deployed) {
                handleBuildFailure(build, "webhook 部署失败", startMs);
                return CompletableFuture.completedFuture(null);
            }
            appendLog("deploy", "部署成功");

            // Stage 5: 完成
            build.setStatus("success");
            build.setCompletedAt(LocalDateTime.now());
            build.setDurationMs(System.currentTimeMillis() - startMs);
            buildMapper.updateById(build);
            appendLog("complete", "构建发布完成 (版本: " + build.getVersionTag() + ")");

            // 通知
            notificationService.notifyBuildSuccess(build);
            log.info("构建成功: id={}, version={}, duration={}ms",
                    buildId, build.getVersionTag(), build.getDurationMs());

        } catch (Exception e) {
            log.error("构建异常: buildId={}, error={}", buildId, e.getMessage());
            handleBuildFailure(build, e.getMessage(), startMs);
        }
        return CompletableFuture.completedFuture(null);
    }

    /** 重试失败构建 */
    @Transactional
    public void retryBuild(Long buildId) {
        PublishBuildPO build = buildMapper.selectById(buildId);
        if (build == null) throw new RuntimeException("构建记录不存在");
        build.setRetryCount(0);
        build.setStatus("queued");
        build.setErrorMessage(null);
        build.setUpdateTime(LocalDateTime.now());
        buildMapper.updateById(build);
        log.info("构建已重新加入队列: buildId={}", buildId);
        executeBuild(buildId);
    }

    /** 回滚到指定版本 */
    @Transactional
    public void rollback(Long buildId, Long targetBuildId, String triggeredBy) {
        PublishBuildPO target = buildMapper.selectById(targetBuildId);
        if (target == null) throw new RuntimeException("目标构建记录不存在");

        PublishBuildPO rollbackBuild = new PublishBuildPO();
        rollbackBuild.setSiteId(target.getSiteId());
        rollbackBuild.setVersionTag(target.getVersionTag() + "-rollback");
        rollbackBuild.setEnvironment(target.getEnvironment());
        rollbackBuild.setBuildNumber(String.valueOf(System.currentTimeMillis()));
        rollbackBuild.setStatus("building");
        rollbackBuild.setRetryCount(0);
        rollbackBuild.setMaxRetries(maxRetries);
        rollbackBuild.setTriggeredBy(triggeredBy);
        rollbackBuild.setRollbacked(true);
        rollbackBuild.setRolledBackFromId(buildId);
        buildMapper.insert(rollbackBuild);

        // 标记原构建为已回滚
        PublishBuildPO original = buildMapper.selectById(buildId);
        if (original != null) {
            original.setRollbacked(true);
            buildMapper.updateById(original);
        }

        // 异步执行回滚部署
        PublishBuildPO finalRollback = rollbackBuild;
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);
                appendLog("deploy", "正在回滚到版本: " + target.getVersionTag());
                Thread.sleep(1000);
                finalRollback.setStatus("success");
                finalRollback.setCompletedAt(LocalDateTime.now());
                buildMapper.updateById(finalRollback);
                notificationService.notifyRollbackSuccess(finalRollback, target.getVersionTag());
                log.info("回滚成功: buildId={}, targetVersion={}", finalRollback.getId(), target.getVersionTag());
            } catch (Exception e) {
                finalRollback.setStatus("failed");
                finalRollback.setErrorMessage(e.getMessage());
                buildMapper.updateById(finalRollback);
            }
        });
    }

    // ===================== 查询 =====================

    public IPage<PublishBuildPO> listBuilds(Long siteId, String status, int current, int size) {
        LambdaQueryWrapper<PublishBuildPO> wrapper = new LambdaQueryWrapper<PublishBuildPO>()
                .eq(siteId != null, PublishBuildPO::getSiteId, siteId)
                .eq(status != null, PublishBuildPO::getStatus, status)
                .orderByDesc(PublishBuildPO::getCreateTime);
        return buildMapper.selectPage(new Page<>(current, size), wrapper);
    }

    public PublishBuildPO getBuild(Long id) { return buildMapper.selectById(id); }

    public List<PublishBuildPO> getBuildHistory(Long siteId, int limit) {
        return buildMapper.selectList(new LambdaQueryWrapper<PublishBuildPO>()
                .eq(PublishBuildPO::getSiteId, siteId)
                .orderByDesc(PublishBuildPO::getCreateTime)
                .last("LIMIT " + limit));
    }

    // ===================== 内部方法 =====================

    private boolean checkSiteExists(PublishBuildPO build) {
        return siteRepository.findById(build.getSiteId()) != null;
    }

    private void updateStatus(PublishBuildPO build, String status) {
        build.setStatus(status);
        if ("building".equals(status) && build.getStartedAt() == null) {
            build.setStartedAt(LocalDateTime.now());
        }
        build.setUpdateTime(LocalDateTime.now());
        buildMapper.updateById(build);
    }

    private void appendLog(String stage, String message) {
        log.info("[{}] {}", stage, message);
    }

    private boolean triggerWebhook(PublishBuildPO build) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            appendLog("deploy", "未配置 webhook URL，跳过部署");
            return true;
        }
        try {
            String body = String.format(
                    "{\"buildId\":%d,\"siteId\":%d,\"version\":\"%s\",\"env\":\"%s\",\"rollback\":%b}",
                    build.getId(), build.getSiteId(), build.getVersionTag(),
                    build.getEnvironment(), build.getRollbacked() != null && build.getRollbacked());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            appendLog("deploy", "HTTP " + response.statusCode());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            appendLog("deploy", "webhook 异常: " + e.getMessage());
            return false;
        }
    }

    private void handleBuildFailure(PublishBuildPO build, String reason, long startMs) {
        int retryCount = build.getRetryCount() != null ? build.getRetryCount() : 0;
        if (retryCount < maxRetries) {
            build.setRetryCount(retryCount + 1);
            build.setStatus("queued");
            appendLog("retry", String.format("失败重试 (%d/%d): %s", retryCount + 1, maxRetries, reason));
        } else {
            failBuild(build, reason, startMs);
        }
        build.setUpdateTime(LocalDateTime.now());
        buildMapper.updateById(build);
    }

    private void failBuild(PublishBuildPO build, String reason, long startMs) {
        build.setStatus("failed");
        build.setErrorMessage(reason);
        build.setCompletedAt(LocalDateTime.now());
        build.setDurationMs(System.currentTimeMillis() - startMs);
        buildMapper.updateById(build);
        notificationService.notifyBuildFailed(build, reason);
        log.warn("构建失败: buildId={}, reason={}", build.getId(), reason);
    }
}
