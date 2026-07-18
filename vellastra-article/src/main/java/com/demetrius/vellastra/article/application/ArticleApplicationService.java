package com.demetrius.vellastra.article.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.article.domain.article.entity.Article;
import com.demetrius.vellastra.article.domain.article.repository.ArticleRepository;
import com.demetrius.vellastra.article.domain.article.valueobject.ArticleStatus;
import com.demetrius.vellastra.article.interfaces.dto.*;
import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.common.response.PageResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: ArticleApplicationService</p>
 * <p>Description: 文章应用服务，负责文章的 CRUD、发布、置顶、浏览计数、点赞等业务逻辑</p>
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
@Service
public class ArticleApplicationService {

    private final ArticleRepository articleRepository;

    public ArticleApplicationService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 新建文章
     *
     * @param request 创建文章请求
     * @param userId  作者ID
     * @return 文章ID
     */
    public Long createArticle(CreateArticleRequest request, Long userId) {
        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .summary(request.getSummary())
                .coverImage(request.getCoverImage())
                .categoryId(request.getCategoryId())
                .status(request.getStatus() != null ? request.getStatus() : 0)
                .authorId(userId)
                .viewCount(0L)
                .likeCount(0L)
                .build();
        article.initCreateTime();
        articleRepository.save(article);
        return article.getId();
    }

    /**
     * 更新文章
     *
     * @param id      文章ID
     * @param request 更新文章请求
     */
    public void updateArticle(Long id, UpdateArticleRequest request) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }
        article.updateTime();
        articleRepository.save(article);
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        if (article.isPublished()) {
            throw ErrorCode.ARTICLE_PUBLISHED.toException();
        }
        articleRepository.delete(id);
    }

    /**
     * 根据ID查看文章
     *
     * @param id 文章ID
     * @return 文章视图对象
     */
    public ArticleVO getArticleById(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        return toVO(article);
    }

    /**
     * 分页查询文章列表（支持多条件筛选：分类、关键词、标签、作者）
     *
     * @param current   页码
     * @param size      每页条数
     * @param categoryId 分类ID（可选）
     * @param keyword   关键词（可选，匹配标题）
     * @param tag       标签（可选）
     * @param authorId  作者ID（可选）
     * @return 分页文章列表
     */
    public PageResult<ArticleVO> listArticles(long current, long size, Long categoryId,
                                              String keyword, Long authorId) {
        Page<Article> page = articleRepository.findPage(current, size, categoryId, keyword, authorId);
        return PageResult.of(
                page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), current, size
        );
    }

    /**
     * 发布文章（草稿→已发布）
     *
     * @param id 文章ID
     */
    public void publish(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        article.publish();
        article.setPublishTime(LocalDateTime.now());
        articleRepository.save(article);
    }

    /**
     * 撤回发布（已发布→下架）
     *
     * @param id 文章ID
     */
    public void withdraw(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        if (!article.isPublished()) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        article.setStatus(ArticleStatus.OFFLINE.getCode());
        article.updateTime();
        articleRepository.save(article);
    }

    /**
     * 设置/取消置顶
     *
     * @param id  文章ID
     * @param top true=置顶, false=取消置顶
     */
    public void topArticle(Long id, boolean top) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        article.setIsTop(top ? 1 : 0);
        article.updateTime();
        articleRepository.save(article);
    }

    /**
     * 增加浏览量（防刷逻辑后续在此扩展）
     *
     * @param id 文章ID
     */
    public void incrementViewCount(Long id) {
        articleRepository.updateViewCount(id);
    }

    /**
     * 点赞/取消点赞（toggle 模式）
     *
     * @param id     文章ID
     * @param userId 用户ID
     */
    public void toggleLike(Long id, Long userId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw ErrorCode.ARTICLE_NOT_FOUND.toException();
        }
        boolean liked = articleRepository.toggleLike(id, userId);
        article.setLikeCount(article.getLikeCount() + (liked ? 1 : -1));
        articleRepository.save(article);
    }

    /**
     * 获取最新文章
     *
     * @param size 获取数量
     * @return 最新文章列表
     */
    public List<ArticleVO> getLatestArticles(int size) {
        List<Article> articles = articleRepository.findLatest(size);
        return articles.stream().map(this::toVO).toList();
    }

    /**
     * 批量操作（删除/发布）
     *
     * @param request 批量操作请求（ids + action）
     */
    public void batchOperation(BatchArticleRequest request) {
        List<Long> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return;
        }
        switch (request.getAction()) {
            case "delete" -> ids.forEach(id -> {
                Article article = articleRepository.findById(id);
                if (article != null && !article.isPublished()) {
                    articleRepository.delete(id);
                }
            });
            case "publish" -> ids.forEach(id -> {
                Article article = articleRepository.findById(id);
                if (article != null) {
                    article.publish();
                    article.setPublishTime(LocalDateTime.now());
                    articleRepository.save(article);
                }
            });
            default -> throw new IllegalArgumentException("Unsupported batch action: " + request.getAction());
        }
    }

    /**
     * 领域对象转视图对象
     *
     * @param article 文章领域对象
     * @return 文章视图对象
     */
    private ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setContent(article.getContent());
        vo.setContentHtml(article.getContentHtml());
        vo.setSummary(article.getSummary());
        vo.setCoverImage(article.getCoverImage());
        vo.setCategoryId(article.getCategoryId());
        vo.setStatus(article.getStatus());
        vo.setAuthorId(article.getAuthorId());
        vo.setViewCount(article.getViewCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setIsTop(article.getIsTop());
        vo.setCommentCount(article.getCommentCount());
        vo.setPublishTime(article.getPublishTime());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        vo.setSeoTitle(article.getSeoTitle());
        vo.setSeoDescription(article.getSeoDescription());
        vo.setSeoKeywords(article.getSeoKeywords());
        return vo;
    }
}
