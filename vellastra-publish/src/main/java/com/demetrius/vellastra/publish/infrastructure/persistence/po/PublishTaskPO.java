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
    private String action;
    private String status;
    private String triggerUrl;
    private String resultLog;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
