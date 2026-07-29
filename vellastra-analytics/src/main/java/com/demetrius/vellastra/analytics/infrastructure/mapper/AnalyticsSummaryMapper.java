package com.demetrius.vellastra.analytics.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.analytics.infrastructure.po.AnalyticsSummaryPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnalyticsSummaryMapper extends BaseMapper<AnalyticsSummaryPO> {
}
