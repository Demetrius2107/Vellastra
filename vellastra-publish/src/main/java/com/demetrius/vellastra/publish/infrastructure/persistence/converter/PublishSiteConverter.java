package com.demetrius.vellastra.publish.infrastructure.persistence.converter;

import com.demetrius.vellastra.publish.domain.site.entity.PublishSite;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishSitePO;
import org.springframework.stereotype.Component;

@Component
public class PublishSiteConverter {
    public PublishSite toDomain(PublishSitePO po) {
        if (po == null) return null;
        return PublishSite.builder()
                .id(po.getId()).name(po.getName()).slug(po.getSlug())
                .description(po.getDescription()).repoUrl(po.getRepoUrl())
                .buildCommand(po.getBuildCommand()).outputDir(po.getOutputDir())
                .domain(po.getDomain()).notifyEmail(po.getNotifyEmail())
                .maxBuildRetention(po.getMaxBuildRetention())
                .concurrentBuild(po.getConcurrentBuild()).status(po.getStatus())
                .createTime(po.getCreateTime()).updateTime(po.getUpdateTime()).build();
    }
    public PublishSitePO toPO(PublishSite domain) {
        if (domain == null) return null;
        PublishSitePO po = new PublishSitePO();
        po.setId(domain.getId()); po.setName(domain.getName()); po.setSlug(domain.getSlug());
        po.setDescription(domain.getDescription()); po.setRepoUrl(domain.getRepoUrl());
        po.setBuildCommand(domain.getBuildCommand()); po.setOutputDir(domain.getOutputDir());
        po.setDomain(domain.getDomain()); po.setNotifyEmail(domain.getNotifyEmail());
        po.setMaxBuildRetention(domain.getMaxBuildRetention());
        po.setConcurrentBuild(domain.getConcurrentBuild()); po.setStatus(domain.getStatus());
        po.setCreateTime(domain.getCreateTime()); po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
