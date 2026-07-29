package com.demetrius.vellastra.analytics.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_analytics_summary")
public class AnalyticsSummaryPO {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 统计日期 */
    private LocalDate statDate;

    /** 维度: article_view / user_register / comment_count */
    private String metric;

    /** 统计值 */
    private Long value;

    /** 额外维度（如 categoryId, authorId） */
    private String dimension;

    /** 创建时间 */
    private LocalDateTime createTime;
}
