package com.demetrius.vellastra.analytics.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("t_analytics_daily")
public class AnalyticsDailyPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private String metric;
    private Long value;
    private String dimension;
    private java.time.LocalDateTime createTime;
}
