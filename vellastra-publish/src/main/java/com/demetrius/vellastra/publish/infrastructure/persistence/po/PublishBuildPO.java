package com.demetrius.vellastra.publish.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_publish_build")
public class PublishBuildPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long siteId;
    private String versionTag;
    private String environment;
    private String buildNumber;
    private String status;
    private Integer retryCount;
    private Integer maxRetries;
    private String triggeredBy;
    private String commitSha;
    private String commitMessage;
    private String branch;
    private String errorMessage;
    private Long durationMs;
    private Boolean rollbacked;
    private Long rolledBackFromId;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
