package com.demetrius.vellastra.tag.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.tag.infrastructure.persistence.po.ArticleTagPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: ArticleTagMapper</p>
 * <p>Description: 文章-标签关联 Mapper（MyBatis-Plus）</p>
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
public interface ArticleTagMapper extends BaseMapper<ArticleTagPO> {
}