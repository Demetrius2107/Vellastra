package com.demetrius.vellastra.column.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.column.infrastructure.persistence.mapper.ColumnArticleMapper;
import com.demetrius.vellastra.column.infrastructure.persistence.mapper.ColumnMapper;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnArticlePO;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnPO;
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
class ColumnArticleServiceTest {

    @Mock
    private ColumnArticleMapper articleMapper;

    private ColumnArticleService articleService;

    @BeforeEach
    void setUp() {
        articleService = new ColumnArticleService(articleMapper);
    }

    @Test
    @DisplayName("getArticles 应返回专栏文章列表")
    void getArticles_shouldReturnList() {
        ColumnArticlePO po = new ColumnArticlePO();
        po.setId(1L);
        po.setColumnId(1L);
        po.setArticleId(10L);
        when(articleMapper.findByColumnId(1L)).thenReturn(List.of(po));

        List<ColumnArticlePO> articles = articleService.getArticles(1L);

        assertEquals(1, articles.size());
        assertEquals(10L, articles.get(0).getArticleId());
    }

    @Test
    @DisplayName("addArticle 应插入收录记录")
    void addArticle_shouldInsert() {
        articleService.addArticle(1L, 10L, "测试文章", "推荐");

        verify(articleMapper).insert(any(ColumnArticlePO.class));
    }

    @Test
    @DisplayName("batchAddArticles 应批量插入")
    void batchAddArticles_shouldInsertAll() {
        articleService.batchAddArticles(1L, List.of(10L, 11L), List.of("文章A", "文章B"));

        verify(articleMapper, times(2)).insert(any(ColumnArticlePO.class));
    }

    @Test
    @DisplayName("removeArticle 应删除收录")
    void removeArticle_shouldDelete() {
        articleService.removeArticle(1L);
        verify(articleMapper).deleteById(1L);
    }
}
