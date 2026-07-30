package com.demetrius.vellastra.publish.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_publish_site")
public class PublishSitePO {
    @TableId(type = IdType.AUTO)
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
}
