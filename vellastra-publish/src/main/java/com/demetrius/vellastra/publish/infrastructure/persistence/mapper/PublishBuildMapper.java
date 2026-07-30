package com.demetrius.vellastra.publish.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishBuildPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PublishBuildMapper extends BaseMapper<PublishBuildPO> {
}
