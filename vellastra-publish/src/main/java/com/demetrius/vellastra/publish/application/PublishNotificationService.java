package com.demetrius.vellastra.publish.application;

import com.demetrius.vellastra.publish.domain.site.entity.PublishSite;
import com.demetrius.vellastra.publish.domain.site.repository.PublishSiteRepository;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishBuildPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublishNotificationService {

    private final PublishSiteRepository siteRepository;

    public PublishNotificationService(PublishSiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public void notifyBuildSuccess(PublishBuildPO build) {
        PublishSite site = siteRepository.findById(build.getSiteId());
        String msg = String.format(
                "✅ 构建成功 | %s | 版本: %s | 环境: %s | 耗时: %dms",
                site != null ? site.getName() : "未知站点",
                build.getVersionTag(), build.getEnvironment(), build.getDurationMs());
        log.info("[通知] {}", msg);
        if (site != null && site.getNotifyEmail() != null && !site.getNotifyEmail().isEmpty()) {
            log.info("[邮件通知] 发送到: {} | {}", site.getNotifyEmail(), msg);
        }
    }

    public void notifyBuildFailed(PublishBuildPO build, String reason) {
        PublishSite site = siteRepository.findById(build.getSiteId());
        String msg = String.format(
                "❌ 构建失败 | %s | 版本: %s | 原因: %s",
                site != null ? site.getName() : "未知站点",
                build.getVersionTag(), reason);
        log.warn("[通知] {}", msg);
        if (site != null && site.getNotifyEmail() != null && !site.getNotifyEmail().isEmpty()) {
            log.warn("[邮件通知] 发送到: {} | {}", site.getNotifyEmail(), msg);
        }
    }

    public void notifyRollbackSuccess(PublishBuildPO build, String targetVersion) {
        PublishSite site = siteRepository.findById(build.getSiteId());
        String msg = String.format(
                "🔄 回滚成功 | %s | 回滚到版本: %s | 操作人: %s",
                site != null ? site.getName() : "未知站点", targetVersion, build.getTriggeredBy());
        log.info("[通知] {}", msg);
    }
}
