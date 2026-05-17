package com.demetrius.blog.article.interfaces.facade;

import com.demetrius.blog.article.application.ArticleApplicationService;
import com.demetrius.blog.article.interfaces.dto.*;
import com.demetrius.blog.common.response.PageResult;
import com.demetrius.blog.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) Long categoryId) {
        return Result.success(articleApplicationService.listArticles(current, size, categoryId));
    }


    //todo: 发布 下架文章
    public Result<Long> publishAtricle(){
        return Result.success();
    }

    //todo: 根据标题或内容搜索 根据关键词搜索
    //todo:按标签查询
    //todo:按作者查询
    //todo 置顶文章
    //todo 获取最新文章
    //todo 增加浏览量
    //todo 增加点击量

}
