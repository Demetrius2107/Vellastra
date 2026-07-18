package com.demetrius.vellastra.category.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.category.infrastructure.persistence.po.CategoryPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: CategoryMapper</p>
 * <p>Description: 分类 Mapper（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryPO> {
}
