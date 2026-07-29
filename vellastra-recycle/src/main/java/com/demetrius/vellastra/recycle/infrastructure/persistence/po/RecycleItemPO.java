package com.demetrius.vellastra.recycle.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_recycle_item")
public class RecycleItemPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private String itemType;
    private String title;
    private String contentSummary;
    private Long deletedBy;
    private String operator;
    private Integer status;

    /** 保留期限（天） */
    private Integer retentionDays;

    /** 过期时间 */
    private LocalDateTime expireAt;

    /** 删除时间 */
    private LocalDateTime deletedAt;

    /** 恢复时间 */
    private LocalDateTime restoredAt;

    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
