package com.demetrius.vellastra.publish.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_publish_task")
public class PublishTaskPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private String articleTitle;
    private String action;

    /** 状态: queued → building → success / failed */
    private String status;

    /** 当前构建阶段 */
    private String currentStage;

    /** 重试次数 */
    private Integer retryCount;

    /** 最大重试次数 */
    private Integer maxRetries;

    /** 触发人 */
    private String triggeredBy;

    /** webhook 响应日志 */
    private String resultLog;

    /** 构建开始时间 */
    private LocalDateTime startedAt;

    /** 构建完成时间 */
    private LocalDateTime completedAt;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
