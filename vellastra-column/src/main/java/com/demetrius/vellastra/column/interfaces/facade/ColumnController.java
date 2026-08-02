package com.demetrius.vellastra.column.interfaces.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.column.application.ColumnArticleService;
import com.demetrius.vellastra.column.application.ColumnService;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnArticlePO;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnPO;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/column")
public class ColumnController {

    private final ColumnService columnService;
    private final ColumnArticleService columnArticleService;

    public ColumnController(ColumnService columnService, ColumnArticleService columnArticleService) {
        this.columnService = columnService;
        this.columnArticleService = columnArticleService;
    }

    // ===================== 专栏管理 =====================

    @GetMapping
    public Result<IPage<ColumnPO>> list(@RequestParam(defaultValue = "1") int current,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) Boolean featured) {
        return Result.success(columnService.list(current, size, status, featured));
    }

    @GetMapping("/all")
    public Result<List<ColumnPO>> listAll(@RequestParam(required = false) String status) {
        return Result.success(columnService.listAll(status));
    }

    @GetMapping("/{id}")
    public Result<ColumnPO> getById(@PathVariable Long id) {
        return Result.success(columnService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@RequestBody ColumnPO request) {
        return Result.success(columnService.create(request.getName(), request.getSlug(),
                request.getDescription(), request.getCoverImage(),
                request.getAuthorId(), request.getAuthorName(), request.getIsFeatured()));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ColumnPO request) {
        columnService.update(id, request.getName(), request.getDescription(),
                request.getCoverImage(), request.getSortOrder(),
                request.getStatus(), request.getIsFeatured());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { columnService.delete(id); return Result.success(); }

    // ===================== 文章收录 =====================

    @GetMapping("/{columnId}/articles")
    public Result<List<ColumnArticlePO>> getArticles(@PathVariable Long columnId) {
        return Result.success(columnArticleService.getArticles(columnId));
    }

    @PostMapping("/{columnId}/articles")
    public Result<Void> addArticle(@PathVariable Long columnId, @RequestParam Long articleId,
                                   @RequestParam String articleTitle,
                                   @RequestParam(required = false) String note) {
        columnArticleService.addArticle(columnId, articleId, articleTitle, note);
        return Result.success();
    }

    @PostMapping("/{columnId}/articles/batch")
    public Result<Void> batchAddArticles(@PathVariable Long columnId, @RequestBody List<ColumnArticlePO> articles) {
        columnArticleService.batchAddArticles(columnId,
                articles.stream().map(ColumnArticlePO::getArticleId).toList(),
                articles.stream().map(ColumnArticlePO::getArticleTitle).toList());
        return Result.success();
    }

    @DeleteMapping("/articles/{id}")
    public Result<Void> removeArticle(@PathVariable Long id) {
        columnArticleService.removeArticle(id);
        return Result.success();
    }

    @PutMapping("/articles/{id}/sort")
    public Result<Void> updateSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        columnArticleService.updateSortOrder(id, sortOrder);
        return Result.success();
    }
}
