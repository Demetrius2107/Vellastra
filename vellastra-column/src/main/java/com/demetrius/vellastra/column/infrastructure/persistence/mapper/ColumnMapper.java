package com.demetrius.vellastra.column.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ColumnMapper extends BaseMapper<ColumnPO> {
}
