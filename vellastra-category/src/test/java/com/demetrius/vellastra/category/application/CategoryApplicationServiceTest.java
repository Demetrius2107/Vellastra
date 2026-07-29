package com.demetrius.vellastra.category.application;

import com.demetrius.vellastra.category.domain.category.entity.Category;
import com.demetrius.vellastra.category.domain.category.repository.CategoryRepository;
import com.demetrius.vellastra.category.interfaces.dto.CategoryVO;
import com.demetrius.vellastra.category.interfaces.dto.CreateCategoryRequest;
import com.demetrius.vellastra.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @DisplayName("getCategoryTree 应返回树形结构")
    void getCategoryTree_shouldReturnTree() {
        Category parent = Category.builder().id(1L).name("技术").parentId(0L).sort(1).createTime(LocalDateTime.now()).build();
        Category child = Category.builder().id(2L).name("Java").parentId(1L).sort(1).createTime(LocalDateTime.now()).build();
        when(categoryRepository.findAll()).thenReturn(List.of(parent, child));

        List<CategoryVO> tree = categoryApplicationService.getCategoryTree();

        assertEquals(1, tree.size());
        assertEquals("技术", tree.get(0).getName());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("Java", tree.get(0).getChildren().get(0).getName());
    }

    @Test
    @DisplayName("getById 存在时返回分类")
    void getById_existing_shouldReturnVO() {
        Category category = Category.builder().id(1L).name("技术").build();
        when(categoryRepository.findById(1L)).thenReturn(category);

        CategoryVO vo = categoryApplicationService.getById(1L);

        assertEquals("技术", vo.getName());
    }

    @Test
    @DisplayName("getById 不存在时抛出异常")
    void getById_notFound_shouldThrow() {
        when(categoryRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> categoryApplicationService.getById(99L));
    }

    @Test
    @DisplayName("create 应保存分类并返回ID")
    void create_shouldSaveAndReturnId() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("新分类");
        request.setSort(1);

        doAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return null;
        }).when(categoryRepository).save(any());

        Long id = categoryApplicationService.create(request);
        assertEquals(1L, id);
    }

    @Test
    @DisplayName("delete 有子分类时抛出异常")
    void delete_hasChildren_shouldThrow() {
        when(categoryRepository.findById(1L)).thenReturn(Category.builder().id(1L).build());
        when(categoryRepository.existsByParentId(1L)).thenReturn(true);

        assertThrows(BizException.class, () -> categoryApplicationService.delete(1L));
        verify(categoryRepository, never()).delete(any());
    }
}