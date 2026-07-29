package com.demetrius.vellastra.article.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>Title: ArticleTagRelPO</p>
 * <p>Description: 文章-标签关联 PO，用于文章联动写入 t_article_tag</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@TableName("t_article_tag")
public class ArticleTagRelPO {
    private Long id;
    private Long articleId;
    private Long tagId;
}