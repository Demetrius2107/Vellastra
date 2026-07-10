package com.demetrius.vellastra.article.domain.article.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.article.domain.article.entity.Article;

import java.util.List;

/**
 * <p>Title: ArticleRepository</p>
 * <p>Description: 文章仓储接口</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
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
