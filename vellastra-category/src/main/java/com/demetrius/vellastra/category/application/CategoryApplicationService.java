package com.demetrius.vellastra.category.application;

import com.demetrius.vellastra.category.domain.category.entity.Category;
import com.demetrius.vellastra.category.domain.category.repository.CategoryRepository;
import com.demetrius.vellastra.category.interfaces.dto.*;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: CategoryApplicationService</p>
 * <p>Description: 分类应用服务，负责分类的树形结构、CRUD等业务逻辑</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Service
public class CategoryApplicationService {

    private final CategoryRepository categoryRepository;

    public CategoryApplicationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 获取分类树
     *
     * @return 分类树形结构（嵌套 children）
     */
    public List<CategoryVO> getCategoryTree() {
        List<Category> all = categoryRepository.findAll();
        return buildTree(all.stream().map(this::toVO).collect(Collectors.toList()));
    }

    /**
     * 根据ID查看分类
     *
     * @param id 分类ID
     * @return 分类视图对象
     */
    public CategoryVO getById(Long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw ErrorCode.CATEGORY_NOT_FOUND.toException();
        }
        return toVO(category);
    }

    /**
     * 新增分类
     *
     * @param request 创建分类请求
     * @return 分类ID
     */
    public Long create(CreateCategoryRequest request) {
        if (request.getParentId() != null && request.getParentId() > 0) {
            Category parent = categoryRepository.findById(request.getParentId());
            if (parent == null) {
                throw ErrorCode.CATEGORY_NOT_FOUND.toException();
            }
        }

        Category category = Category.builder()
                .name(request.getName())
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .description(request.getDescription())
                .sort(request.getSort() != null ? request.getSort() : 0)
                .build();
        category.initCreateTime();
        categoryRepository.save(category);
        return category.getId();
    }

    /**
     * 更新分类
     *
     * @param id      分类ID
     * @param request 更新分类请求
     */
    public void update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw ErrorCode.CATEGORY_NOT_FOUND.toException();
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getSort() != null) {
            category.setSort(request.getSort());
        }
        category.updateTime();
        categoryRepository.save(category);
    }

    /**
     * 删除分类
     *
     * <p>存在子分类时不可删除</p>
     *
     * @param id 分类ID
     */
    public void delete(Long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw ErrorCode.CATEGORY_NOT_FOUND.toException();
        }

        boolean hasChildren = categoryRepository.existsByParentId(id);
        if (hasChildren) {
            throw new BizException(400, "该分类下存在子分类，无法删除");
        }

        categoryRepository.delete(id);
    }

    private List<CategoryVO> buildTree(List<CategoryVO> all) {
        Map<Long, List<CategoryVO>> groupByParent = all.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() > 0)
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        List<CategoryVO> roots = all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .sorted((a, b) -> {
                    int sa = a.getSort() != null ? a.getSort() : 0;
                    int sb = b.getSort() != null ? b.getSort() : 0;
                    return sa - sb;
                })
                .toList();

        for (CategoryVO root : roots) {
            root.setChildren(buildChildren(root.getId(), groupByParent));
        }
        return new ArrayList<>(roots);
    }

    private List<CategoryVO> buildChildren(Long parentId, Map<Long, List<CategoryVO>> map) {
        List<CategoryVO> children = map.getOrDefault(parentId, new ArrayList<>());
        children.sort((a, b) -> {
            int sa = a.getSort() != null ? a.getSort() : 0;
            int sb = b.getSort() != null ? b.getSort() : 0;
            return sa - sb;
        });
        for (CategoryVO child : children) {
            child.setChildren(buildChildren(child.getId(), map));
        }
        return children;
    }

    private CategoryVO toVO(Category c) {
        CategoryVO vo = new CategoryVO();
        vo.setId(c.getId());
        vo.setName(c.getName());
        vo.setDescription(c.getDescription());
        vo.setSort(c.getSort());
        vo.setParentId(c.getParentId() != null && c.getParentId() > 0 ? c.getParentId() : null);
        vo.setCreateTime(c.getCreateTime());
        return vo;
    }
}
