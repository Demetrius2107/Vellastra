package com.demetrius.vellastra.category.application;

import com.demetrius.vellastra.category.domain.category.entity.Category;
import com.demetrius.vellastra.category.domain.category.repository.CategoryRepository;
import com.demetrius.vellastra.category.interfaces.dto.CreateCategoryRequest;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("create 应保存并返回新分类ID")
    void create_shouldSave() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("技术");
        request.setParentId(0L);
        request.setSort(1);
        request.setDescription("技术相关文章");

        doAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return null;
        }).when(categoryRepository).save(any());

        Long id = categoryApplicationService.create(request);
        assertEquals(1L, id);
    }

    @Test
    @DisplayName("getCategoryTree 应返回树形结构")
    void getCategoryTree_shouldReturnTree() {
        Category parent = Category.builder().id(1L).name("技术").parentId(0L).sort(1).build();
        Category child = Category.builder().id(2L).name("Java").parentId(1L).sort(1).build();
        when(categoryRepository.findAll()).thenReturn(List.of(parent, child));

        var tree = categoryApplicationService.getCategoryTree();
        assertEquals(1, tree.size());
        assertEquals("技术", tree.get(0).getName());
    }

    @Test
    @DisplayName("delete 不存在时抛出异常")
    void delete_notFound_shouldThrow() {
        when(categoryRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> categoryApplicationService.delete(99L));
    }
}