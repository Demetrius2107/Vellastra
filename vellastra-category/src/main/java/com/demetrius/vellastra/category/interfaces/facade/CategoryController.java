package com.demetrius.vellastra.category.interfaces.facade;

import com.demetrius.vellastra.category.application.CategoryApplicationService;
import com.demetrius.vellastra.category.interfaces.dto.CategoryVO;
import com.demetrius.vellastra.category.interfaces.dto.CreateCategoryRequest;
import com.demetrius.vellastra.category.interfaces.dto.UpdateCategoryRequest;
import com.demetrius.vellastra.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>Title: CategoryController</p>
 * <p>Description: 分类 RESTful 接口控制器</p>
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
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryApplicationService categoryApplicationService;

    public CategoryController(CategoryApplicationService categoryApplicationService) {
        this.categoryApplicationService = categoryApplicationService;
    }

    /**
     * 获取分类树
     *
     * @return 分类树形结构
     */
    @GetMapping("/tree")
    public Result<List<CategoryVO>> tree() {
        return Result.success(categoryApplicationService.getCategoryTree());
    }

    /**
     * 查看分类详情
     *
     * @param id 分类ID
     * @return 分类视图对象
     */
    @GetMapping("/{id}")
    public Result<CategoryVO> getCategory(@PathVariable Long id) {
        return Result.success(categoryApplicationService.getById(id));
    }

    /**
     * 新增分类
     *
     * @param request 创建分类请求
     * @return 分类ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateCategoryRequest request) {
        return Result.success(categoryApplicationService.create(request));
    }

    /**
     * 更新分类
     *
     * @param id      分类ID
     * @param request 更新分类请求
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        categoryApplicationService.update(id, request);
        return Result.success();
    }

    /**
     * 删除分类（有子分类或文章时不可删除）
     *
     * @param id 分类ID
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryApplicationService.delete(id);
        return Result.success();
    }
}
