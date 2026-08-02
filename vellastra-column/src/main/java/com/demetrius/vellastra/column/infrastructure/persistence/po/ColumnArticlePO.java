package com.demetrius.vellastra.column.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_column_article")
public class ColumnArticlePO {
    private Long id;
    private Long columnId;
    private Long articleId;
    private String articleTitle;
    private Integer sortOrder;
    private String note;
    private LocalDateTime createTime;
}
