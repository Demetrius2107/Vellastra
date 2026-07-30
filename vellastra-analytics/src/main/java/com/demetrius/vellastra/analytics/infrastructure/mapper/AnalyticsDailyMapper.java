package com.demetrius.vellastra.analytics.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.analytics.infrastructure.po.AnalyticsDailyPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AnalyticsDailyMapper extends BaseMapper<AnalyticsDailyPO> {

    @Select("SELECT COALESCE(SUM(value),0) FROM t_analytics_daily WHERE stat_date = #{date} AND metric = #{metric}")
    long sumByDate(java.time.LocalDate date, String metric);

    @Select("SELECT stat_date, SUM(value) as value FROM t_analytics_daily " +
            "WHERE metric = #{metric} AND stat_date >= #{start} AND stat_date <= #{end} " +
            "GROUP BY stat_date ORDER BY stat_date")
    List<java.util.Map<String, Object>> trend(String metric, java.time.LocalDate start, java.time.LocalDate end);
}
