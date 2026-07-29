package com.demetrius.vellastra.publish.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_publish_build_log")
public class PublishBuildLogPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String stage;
    private String level;
    private String message;
    private LocalDateTime createTime;
}
