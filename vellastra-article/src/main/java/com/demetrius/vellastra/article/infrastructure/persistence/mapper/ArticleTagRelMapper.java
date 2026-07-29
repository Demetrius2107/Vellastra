package com.demetrius.vellastra.article.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.article.infrastructure.persistence.po.ArticleTagRelPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>Title: ArticleTagRelMapper</p>
 * <p>Description: 文章-标签关联 Mapper，用于文章联动写入 t_article_tag</p>
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
public interface ArticleTagRelMapper extends BaseMapper<ArticleTagRelPO> {
}