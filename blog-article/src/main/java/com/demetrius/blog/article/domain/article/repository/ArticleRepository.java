package com.demetrius.blog.article.domain.article.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.blog.article.domain.article.entity.Article;

import java.util.List;

public interface ArticleRepository {

    Article findById(Long id);

    Page<Article> findPage(long current, long size, Long categoryId, String keyword, String tag, Long authorId);

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
