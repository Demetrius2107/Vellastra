package com.demetrius.vellastra.recycle.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private String operator;
    private Integer status;
    private LocalDateTime deletedAt;
    private LocalDateTime expireAt;
    private LocalDateTime createTime;
}
