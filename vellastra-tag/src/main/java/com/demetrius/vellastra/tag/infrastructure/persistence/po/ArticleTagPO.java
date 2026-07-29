package com.demetrius.vellastra.tag.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>Title: ArticleTagPO</p>
 * <p>Description: 文章-标签关联持久化对象，与 t_article_tag 表对应</p>
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
public class ArticleTagPO {

    private Long id;
    private Long articleId;
    private Long tagId;
}