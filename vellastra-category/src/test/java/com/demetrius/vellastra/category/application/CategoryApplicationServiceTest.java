package com.demetrius.vellastra.category.application;

import com.demetrius.vellastra.category.domain.category.entity.Category;
import com.demetrius.vellastra.category.domain.category.repository.CategoryRepository;
import com.demetrius.vellastra.category.interfaces.dto.CreateCategoryRequest;
import com.demetrius.vellastra.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CategoryApplicationService}
 */
@ExtendWith(MockitoExtension.class)
class CategoryApplicationServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryApplicationService categoryApplicationService;

    @BeforeEach
    void setUp() {
        categoryApplicationService = new CategoryApplicationService(categoryRepository);
    }

    @Test
    @DisplayName("createCategory 应保存并返回新分类")
    void createCategory_shouldSave() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("技术");
        request.setParentId(0L);
        request.setSort(1);
        request.setDescription("技术相关文章");

        when(categoryRepository.save(any())).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Category result = categoryApplicationService.createCategory(request);
        assertEquals("技术", result.getName());
        assertEquals(0L, result.getParentId());
        assertNotNull(result.getCreateTime());
    }

    @Test
    @DisplayName("getCategoryTree 应返回树形结构")
    void getCategoryTree_shouldReturnTree() {
        Category parent = new Category(1L, "技术", 0L, 1, "技术类", null, null);
        Category child = new Category(2L, "Java", 1L, 1, "Java相关", null, null);
        when(categoryRepository.findAll()).thenReturn(List.of(parent, child));

        var tree = categoryApplicationService.getCategoryTree();
        assertEquals(1, tree.size());
        assertEquals("技术", tree.get(0).getName());
    }

    @Test
    @DisplayName("deleteCategory 不存在时抛出异常")
    void deleteCategory_notFound_shouldThrow() {
        when(categoryRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> categoryApplicationService.deleteCategory(99L));
    }
}