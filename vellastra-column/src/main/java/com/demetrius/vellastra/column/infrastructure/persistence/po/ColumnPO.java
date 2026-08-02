package com.demetrius.vellastra.column.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_column")
public class ColumnPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String coverImage;
    private Long authorId;
    private String authorName;
    private String status;
    private Integer articleCount;
    private Integer sortOrder;
    private Boolean isFeatured;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
