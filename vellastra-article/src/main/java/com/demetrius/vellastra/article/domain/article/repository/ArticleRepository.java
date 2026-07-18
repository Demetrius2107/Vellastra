package com.demetrius.vellastra.article.domain.article.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.article.domain.article.entity.Article;

import java.util.List;

/**
 * <p>Title: ArticleRepository</p>
 * <p>Description: 文章仓储接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface ArticleRepository {

    Article findById(Long id);

    Page<Article> findPage(long current, long size, Long categoryId, String keyword, Long authorId);

    void save(Article article);

    void delete(Long id);

    void updateViewCount(Long id);

    /**
     * 点赞切换
     * @return true = 已点赞（新增），false = 取消点赞
     */
    boolean toggleLike(Long articleId, Long userId);

    List<Article> findLatest(int size);
}
