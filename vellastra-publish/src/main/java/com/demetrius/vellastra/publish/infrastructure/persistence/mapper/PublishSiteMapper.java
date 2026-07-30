package com.demetrius.vellastra.publish.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishSitePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PublishSiteMapper extends BaseMapper<PublishSitePO> {
}
