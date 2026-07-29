package com.demetrius.vellastra.tag.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.tag.infrastructure.persistence.po.TagPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: TagMapper</p>
 * <p>Description: 标签 Mapper（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Mapper
public interface TagMapper extends BaseMapper<TagPO> {
}