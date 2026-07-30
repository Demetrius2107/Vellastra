package com.demetrius.vellastra.publish.domain.site.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishSite {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String repoUrl;
    private String buildCommand;
    private String outputDir;
    private String domain;
    private String notifyEmail;
    private Integer maxBuildRetention;
    private Boolean concurrentBuild;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}
