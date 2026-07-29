package com.demetrius.vellastra.recycle.infrastructure.persistence.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface RecycleItemMapper extends BaseMapper<RecycleItemPO> {}
