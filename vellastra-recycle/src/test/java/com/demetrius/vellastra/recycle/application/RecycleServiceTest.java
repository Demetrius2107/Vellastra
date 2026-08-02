package com.demetrius.vellastra.recycle.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.recycle.domain.item.entity.RecycleItem;
import com.demetrius.vellastra.recycle.domain.item.repository.RecycleItemRepository;
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

@ExtendWith(MockitoExtension.class)
class RecycleServiceTest {

    @Mock
    private RecycleItemRepository repository;

    private RecycleService recycleService;

    @BeforeEach
    void setUp() {
        recycleService = new RecycleService(repository);
    }

    @Test
    @DisplayName("recycle 应保存回收项并设置过期时间")
    void recycle_shouldSaveItem() {
        recycleService.recycle(1L, "comment", "测试评论", "/path",
                1L, "admin", "comment");

        verify(repository).save(any(RecycleItem.class));
    }

    @Test
    @DisplayName("recycle 文章类型应触发级联回收（评论+点赞）")
    void recycle_article_shouldCascade() {
        recycleService.recycle(1L, "article", "测试文章", "/path",
                1L, "admin", "article");

        // 主项目 + 评论 + 点赞 = 3 次 save
        verify(repository, times(3)).save(any(RecycleItem.class));
    }

    @Test
    @DisplayName("recycle 非文章类型不应级联")
    void recycle_nonArticle_shouldNotCascade() {
        recycleService.recycle(1L, "file", "测试文件", "/path",
                1L, "admin", "file");

        verify(repository, times(1)).save(any(RecycleItem.class));
    }

    @Test
    @DisplayName("restore 应标记为已恢复")
    void restore_shouldMarkRestored() {
        RecycleItem item = RecycleItem.builder().id(1L).status("deleted").build();
        when(repository.findById(1L)).thenReturn(item);

        recycleService.restore(1L);

        assertEquals("restored", item.getStatus());
        assertNotNull(item.getRestoredAt());
        verify(repository).save(item);
    }

    @Test
    @DisplayName("batchRestore 应批量恢复")
    void batchRestore_shouldRestoreAll() {
        when(repository.findById(1L)).thenReturn(RecycleItem.builder().id(1L).status("deleted").build());
        when(repository.findById(2L)).thenReturn(RecycleItem.builder().id(2L).status("deleted").build());

        recycleService.batchRestore(List.of(1L, 2L));

        verify(repository, times(2)).save(any(RecycleItem.class));
    }

    @Test
    @DisplayName("deletePermanently 应删除")
    void deletePermanently_shouldDelete() {
        when(repository.findById(1L)).thenReturn(RecycleItem.builder().id(1L).build());

        recycleService.deletePermanently(1L);

        verify(repository).delete(1L);
    }
}
