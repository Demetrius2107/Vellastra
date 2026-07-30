package com.demetrius.vellastra.recycle.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RecycleItemMapper extends BaseMapper<RecycleItemPO> {
    @Select("SELECT COALESCE(SUM(LENGTH(title)), 0) FROM t_recycle_item WHERE status = 'deleted'")
    long totalStorageBytes();

    @Select("SELECT COUNT(*) FROM t_recycle_item WHERE status = 'deleted'")
    long totalRecycledCount();
}
