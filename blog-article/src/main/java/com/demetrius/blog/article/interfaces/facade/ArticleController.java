package com.demetrius.blog.article.interfaces.facade;

import com.demetrius.blog.article.application.ArticleApplicationService;
import com.demetrius.blog.article.interfaces.dto.*;
import com.demetrius.blog.common.response.PageResult;
import com.demetrius.blog.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 我们在命运的两端 是否有相似的痛感
 * 在每个忏悔的夜晚
 * 可我无法再隐藏 想要倾诉的愿望
 * 只有冷漠以对 她才不会再受伤
 * <p>文章控制器</p>
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleApplicationService articleApplicationService;

    public ArticleController(ArticleApplicationService articleApplicationService) {
        this.articleApplicationService = articleApplicationService;
    }

    // ======================== CRUD基础操作 ========================

    @PostMapping
    public Result<Long> createArticle(@Valid @RequestBody CreateArticleRequest request,
                                      @RequestHeader("X-User-Id") Long userId) {
        return Result.success(articleApplicationService.createArticle(request, userId));
    }

    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id,
                                      @Valid @RequestBody UpdateArticleRequest request) {
        articleApplicationService.updateArticle(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleApplicationService.deleteArticle(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> getArticle(@PathVariable Long id) {
        return Result.success(articleApplicationService.getArticleById(id));
    }

    @GetMapping
    public Result<PageResult<ArticleVO>> listArticles(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long authorId) {
        return Result.success(articleApplicationService.listArticles(
                current, size, categoryId, keyword, tag, authorId));
    }

    // ======================== 文章状态管理 ========================

    /**
     * 发布文章（草稿→已发布）
     */
    @PatchMapping("/{id}/publish")
    public Result<Void> publishArticle(@PathVariable Long id) {
        articleApplicationService.publish(id);
        return Result.success();
    }

    /**
     * 撤回发布（已发布→下架）
     */
    @PatchMapping("/{id}/withdraw")
    public Result<Void> withdrawArticle(@PathVariable Long id) {
        articleApplicationService.withdraw(id);
        return Result.success();
    }

    // ======================== 置顶/取消置顶 ========================

    /**
     * 设置/取消置顶
     */
    @PatchMapping("/{id}/top")
    public Result<Void> topArticle(@PathVariable Long id, @RequestParam boolean top) {
        articleApplicationService.topArticle(id, top);
        return Result.success();
    }

    // ======================== 互动统计 ========================

    /**
     * 浏览计数（IP+时间窗口防刷逻辑由 service 层处理）
     */
    @PostMapping("/{id}/view")
    public Result<Void> viewArticle(@PathVariable Long id) {
        articleApplicationService.incrementViewCount(id);
        return Result.success();
    }

    /**
     * 点赞/取消点赞（toggle 模式）
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@PathVariable Long id,
                                    @RequestHeader("X-User-Id") Long userId) {
        articleApplicationService.toggleLike(id, userId);
        return Result.success();
    }

    // ======================== 最新文章 ========================

    @GetMapping("/latest")
    public Result<List<ArticleVO>> getLatestArticle(@RequestParam(defaultValue = "5") int size) {
        return Result.success(articleApplicationService.getLatestArticles(size));
    }

    // ======================== 批量操作 ========================

    @PostMapping("/batch")
    public Result<Void> batchOperation(@Valid @RequestBody BatchArticleRequest request) {
        articleApplicationService.batchOperation(request);
        return Result.success();
    }

}
