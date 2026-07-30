package com.demetrius.vellastra.publish.application;

import com.demetrius.vellastra.publish.domain.site.entity.PublishSite;
import com.demetrius.vellastra.publish.domain.site.repository.PublishSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PublishSiteService {
    private final PublishSiteRepository siteRepository;

    public PublishSiteService(PublishSiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public List<PublishSite> listAll() { return siteRepository.findAll(); }
    public PublishSite getById(Long id) { return siteRepository.findById(id); }

    @Transactional
    public Long create(String name, String slug, String repoUrl, String buildCommand,
                       String outputDir, String domain, String notifyEmail) {
        PublishSite site = PublishSite.builder()
                .name(name).slug(slug).description("").repoUrl(repoUrl)
                .buildCommand(buildCommand != null ? buildCommand : "npm run build")
                .outputDir(outputDir != null ? outputDir : "dist")
                .domain(domain).notifyEmail(notifyEmail)
                .maxBuildRetention(30).concurrentBuild(false)
                .status("active").build();
        site.initCreateTime();
        siteRepository.save(site);
        log.info("站点创建成功: id={}, name={}, slug={}", site.getId(), name, slug);
        return site.getId();
    }

    @Transactional
    public void update(Long id, String name, String description, String repoUrl,
                       String buildCommand, String outputDir, String domain, String notifyEmail) {
        PublishSite site = siteRepository.findById(id);
        if (site == null) throw new RuntimeException("站点不存在");
        if (name != null) site.setName(name);
        if (description != null) site.setDescription(description);
        if (repoUrl != null) site.setRepoUrl(repoUrl);
        if (buildCommand != null) site.setBuildCommand(buildCommand);
        if (outputDir != null) site.setOutputDir(outputDir);
        if (domain != null) site.setDomain(domain);
        if (notifyEmail != null) site.setNotifyEmail(notifyEmail);
        site.updateTime();
        siteRepository.save(site);
        log.info("站点更新成功: id={}", id);
    }

    @Transactional
    public void delete(Long id) {
        siteRepository.delete(id);
        log.info("站点删除成功: id={}", id);
    }
}
