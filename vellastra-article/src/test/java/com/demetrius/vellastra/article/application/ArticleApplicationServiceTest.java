package com.demetrius.vellastra.article.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.article.domain.article.entity.Article;
import com.demetrius.vellastra.article.domain.article.repository.ArticleRepository;
import com.demetrius.vellastra.article.domain.article.valueobject.ArticleStatus;
import com.demetrius.vellastra.article.interfaces.dto.*;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.response.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 文章应用服务单元测试（Mock 仓储层）
 */
@DisplayName("ArticleApplicationService 应用服务")
@ExtendWith(MockitoExtension.class)
class ArticleApplicationServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Captor
    private ArgumentCaptor<Article> articleCaptor;

    private ArticleApplicationService articleApplicationService;

    private Article draftArticle;
    private Article publishedArticle;
    private Article offlineArticle;

    @BeforeEach
    void setUp() {
        articleApplicationService = new ArticleApplicationService(articleRepository);

        LocalDateTime now = LocalDateTime.now();

        draftArticle = Article.builder()
                .id(1L)
                .title("草稿文章")
                .content("草稿内容")
                .summary("草稿摘要")
                .categoryId(1L)
                .status(ArticleStatus.DRAFT.getCode())
                .authorId(100L)
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0)
                .isTop(0)
                .createTime(now)
                .updateTime(now)
                .build();

        publishedArticle = Article.builder()
                .id(2L)
                .title("已发布文章")
                .content("发布内容")
                .summary("发布摘要")
                .categoryId(1L)
                .status(ArticleStatus.PUBLISHED.getCode())
                .authorId(100L)
                .viewCount(100L)
                .likeCount(10L)
                .commentCount(5)
                .isTop(1)
                .publishTime(now)
                .createTime(now)
                .updateTime(now)
                .build();

        offlineArticle = Article.builder()
                .id(3L)
                .title("下架文章")
                .content("下架内容")
                .categoryId(1L)
                .status(ArticleStatus.OFFLINE.getCode())
                .authorId(100L)
                .viewCount(50L)
                .likeCount(5L)
                .commentCount(2)
                .isTop(0)
                .publishTime(now)
                .createTime(now)
                .updateTime(now)
                .build();
    }

    // ======================== 创建文章 ========================

    @Nested
    @DisplayName("创建文章")
    class CreateArticle {

        @Test
        @DisplayName("应创建草稿文章并返回ID")
        void shouldCreateDraftArticle() {
            // given
            CreateArticleRequest request = new CreateArticleRequest();
            request.setTitle("新文章");
            request.setContent("新内容");
            request.setSummary("新摘要");
            request.setCoverImage("https://example.com/cover.jpg");
            request.setCategoryId(1L);
            // status 不传，默认 0（草稿）

            doAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(10L);
                return null;
            }).when(articleRepository).save(any(Article.class));

            // when
            Long articleId = articleApplicationService.createArticle(request, 100L);

            // then
            assertNotNull(articleId);
            assertEquals(10L, articleId);

            verify(articleRepository).save(articleCaptor.capture());
            Article saved = articleCaptor.getValue();

            assertEquals("新文章", saved.getTitle());
            assertEquals("新内容", saved.getContent());
            assertEquals("新摘要", saved.getSummary());
            assertEquals("https://example.com/cover.jpg", saved.getCoverImage());
            assertEquals(1L, saved.getCategoryId());
            assertEquals(ArticleStatus.DRAFT.getCode(), saved.getStatus());
            assertEquals(100L, saved.getAuthorId());
            assertEquals(0L, saved.getViewCount());
            assertEquals(0L, saved.getLikeCount());
            assertNotNull(saved.getCreateTime());
            assertNotNull(saved.getUpdateTime());
        }

        @Test
        @DisplayName("应按照请求中的状态创建文章")
        void shouldCreateArticleWithSpecifiedStatus() {
            // given
            CreateArticleRequest request = new CreateArticleRequest();
            request.setTitle("直接发布的文章");
            request.setContent("内容");
            request.setStatus(ArticleStatus.PUBLISHED.getCode());

            doAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(11L);
                return null;
            }).when(articleRepository).save(any(Article.class));

            // when
            Long articleId = articleApplicationService.createArticle(request, 100L);

            // then
            assertEquals(11L, articleId);

            verify(articleRepository).save(articleCaptor.capture());
            assertEquals(ArticleStatus.PUBLISHED.getCode(), articleCaptor.getValue().getStatus());
        }
    }

    // ======================== 更新文章 ========================

    @Nested
    @DisplayName("更新文章")
    class UpdateArticle {

        @Test
        @DisplayName("应更新文章所有字段")
        void shouldUpdateAllFields() {
            // given
            when(articleRepository.findById(1L)).thenReturn(draftArticle);

            UpdateArticleRequest request = new UpdateArticleRequest();
            request.setTitle("更新后的标题");
            request.setContent("更新后的内容");
            request.setSummary("更新后的摘要");
            request.setCoverImage("https://example.com/new-cover.jpg");
            request.setCategoryId(2L);
            request.setStatus(ArticleStatus.REVIEWING.getCode());

            // when
            articleApplicationService.updateArticle(1L, request);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            Article saved = articleCaptor.getValue();

            assertEquals("更新后的标题", saved.getTitle());
            assertEquals("更新后的内容", saved.getContent());
            assertEquals("更新后的摘要", saved.getSummary());
            assertEquals("https://example.com/new-cover.jpg", saved.getCoverImage());
            assertEquals(2L, saved.getCategoryId());
            assertEquals(ArticleStatus.REVIEWING.getCode(), saved.getStatus());
            assertNotNull(saved.getUpdateTime());
        }

        @Test
        @DisplayName("更新不存在的文章应抛出异常")
        void shouldThrowWhenArticleNotFound() {
            // given
            when(articleRepository.findById(999L)).thenReturn(null);

            UpdateArticleRequest request = new UpdateArticleRequest();
            request.setTitle("标题");
            request.setContent("内容");

            // when & then
            BizException exception = assertThrows(BizException.class,
                    () -> articleApplicationService.updateArticle(999L, request));
            assertEquals(3001, exception.getCode());
            assertEquals("文章不存在", exception.getMessage());
        }

        @Test
        @DisplayName("不传 status 时不应修改状态")
        void shouldNotChangeStatusWhenNotProvided() {
            // given
            when(articleRepository.findById(1L)).thenReturn(draftArticle);

            UpdateArticleRequest request = new UpdateArticleRequest();
            request.setTitle("新标题");
            request.setContent("新内容");
            // status 为 null

            // when
            articleApplicationService.updateArticle(1L, request);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            assertEquals(ArticleStatus.DRAFT.getCode(), articleCaptor.getValue().getStatus());
        }
    }

    // ======================== 删除文章 ========================

    @Nested
    @DisplayName("删除文章")
    class DeleteArticle {

        @Test
        @DisplayName("应删除草稿文章")
        void shouldDeleteDraftArticle() {
            // given
            when(articleRepository.findById(1L)).thenReturn(draftArticle);

            // when
            articleApplicationService.deleteArticle(1L);

            // then
            verify(articleRepository).delete(1L);
        }

        @Test
        @DisplayName("删除已发布文章应抛出异常")
        void shouldThrowWhenDeletingPublishedArticle() {
            // given
            when(articleRepository.findById(2L)).thenReturn(publishedArticle);

            // when & then
            BizException exception = assertThrows(BizException.class,
                    () -> articleApplicationService.deleteArticle(2L));
            assertEquals(3002, exception.getCode());
            assertEquals("文章已发布，无法删除", exception.getMessage());
            verify(articleRepository, never()).delete(anyLong());
        }

        @Test
        @DisplayName("删除不存在的文章应抛出异常")
        void shouldThrowWhenArticleNotFound() {
            // given
            when(articleRepository.findById(999L)).thenReturn(null);

            // when & then
            assertThrows(BizException.class,
                    () -> articleApplicationService.deleteArticle(999L));
        }

        @Test
        @DisplayName("应删除下架文章")
        void shouldDeleteOfflineArticle() {
            // given
            when(articleRepository.findById(3L)).thenReturn(offlineArticle);

            // when
            articleApplicationService.deleteArticle(3L);

            // then
            verify(articleRepository).delete(3L);
        }
    }

    // ======================== 查询文章 ========================

    @Nested
    @DisplayName("查询文章")
    class GetArticle {

        @Test
        @DisplayName("应按 ID 返回文章 VO")
        void shouldReturnArticleVO() {
            // given
            when(articleRepository.findById(2L)).thenReturn(publishedArticle);

            // when
            ArticleVO vo = articleApplicationService.getArticleById(2L);

            // then
            assertNotNull(vo);
            assertEquals(2L, vo.getId());
            assertEquals("已发布文章", vo.getTitle());
            assertEquals("发布内容", vo.getContent());
            assertEquals("发布摘要", vo.getSummary());
            assertEquals(100L, vo.getAuthorId());
            assertEquals(100L, vo.getViewCount());
            assertEquals(10L, vo.getLikeCount());
            assertEquals(5, vo.getCommentCount());
            assertEquals(1, vo.getIsTop());
        }

        @Test
        @DisplayName("查询不存在的文章应抛出异常")
        void shouldThrowWhenArticleNotFound() {
            // given
            when(articleRepository.findById(999L)).thenReturn(null);

            // when & then
            assertThrows(BizException.class,
                    () -> articleApplicationService.getArticleById(999L));
        }
    }

    // ======================== 分页查询 ========================

    @Nested
    @DisplayName("分页查询文章列表")
    class ListArticles {

        @Test
        @DisplayName("应返回分页文章列表")
        void shouldReturnPagedArticles() {
            // given
            Page<Article> page = new Page<>(1, 10, 2);
            page.setRecords(List.of(publishedArticle, draftArticle));
            when(articleRepository.findPage(1, 10, null, null, null)).thenReturn(page);

            // when
            PageResult<ArticleVO> result = articleApplicationService.listArticles(1, 10, null, null, null);

            // then
            assertNotNull(result);
            assertEquals(2, result.getTotal());
            assertEquals(1, result.getCurrent());
            assertEquals(10, result.getSize());
            assertEquals(1, result.getPages());
            assertEquals(2, result.getRecords().size());
            assertEquals("已发布文章", result.getRecords().get(0).getTitle());
            assertEquals("草稿文章", result.getRecords().get(1).getTitle());
        }

        @Test
        @DisplayName("应支持按分类和关键词筛选")
        void shouldSupportFiltering() {
            // given
            Page<Article> page = new Page<>(1, 10, 1);
            page.setRecords(List.of(publishedArticle));
            when(articleRepository.findPage(1, 10, 1L, "发布", 100L)).thenReturn(page);

            // when
            PageResult<ArticleVO> result = articleApplicationService.listArticles(1, 10, 1L, "发布", 100L);

            // then
            assertEquals(1, result.getTotal());
            verify(articleRepository).findPage(1, 10, 1L, "发布", 100L);
        }
    }

    // ======================== 发布/撤回 ========================

    @Nested
    @DisplayName("发布与撤回")
    class PublishAndWithdraw {

        @Test
        @DisplayName("发布文章应将状态设为已发布并设置发布时间")
        void publishShouldSetStatusAndPublishTime() {
            // given
            when(articleRepository.findById(1L)).thenReturn(draftArticle);

            // when
            articleApplicationService.publish(1L);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            Article saved = articleCaptor.getValue();
            assertEquals(ArticleStatus.PUBLISHED.getCode(), saved.getStatus());
            assertNotNull(saved.getPublishTime());
        }

        @Test
        @DisplayName("发布不存在的文章应抛出异常")
        void publishShouldThrowWhenNotFound() {
            // given
            when(articleRepository.findById(999L)).thenReturn(null);

            // when & then
            assertThrows(BizException.class,
                    () -> articleApplicationService.publish(999L));
        }

        @Test
        @DisplayName("撤回应将已发布文章设为下架")
        void withdrawShouldSetOffline() {
            // given
            when(articleRepository.findById(2L)).thenReturn(publishedArticle);

            // when
            articleApplicationService.withdraw(2L);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            assertEquals(ArticleStatus.OFFLINE.getCode(), articleCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("撤回草稿文章应抛出异常")
        void withdrawShouldThrowWhenNotPublished() {
            // given
            when(articleRepository.findById(1L)).thenReturn(draftArticle);

            // when & then
            assertThrows(BizException.class,
                    () -> articleApplicationService.withdraw(1L));
        }
    }

    // ======================== 置顶 ========================

    @Nested
    @DisplayName("置顶/取消置顶")
    class TopArticle {

        @Test
        @DisplayName("置顶应设置 isTop=1")
        void shouldSetTop() {
            // given
            when(articleRepository.findById(1L)).thenReturn(draftArticle);

            // when
            articleApplicationService.topArticle(1L, true);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            assertEquals(1, articleCaptor.getValue().getIsTop());
        }

        @Test
        @DisplayName("取消置顶应设置 isTop=0")
        void shouldUnsetTop() {
            // given
            when(articleRepository.findById(2L)).thenReturn(publishedArticle);

            // when
            articleApplicationService.topArticle(2L, false);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            assertEquals(0, articleCaptor.getValue().getIsTop());
        }

        @Test
        @DisplayName("置顶不存在的文章应抛出异常")
        void shouldThrowWhenArticleNotFound() {
            // given
            when(articleRepository.findById(999L)).thenReturn(null);

            // when & then
            assertThrows(BizException.class,
                    () -> articleApplicationService.topArticle(999L, true));
        }
    }

    // ======================== 浏览计数 ========================

    @Nested
    @DisplayName("浏览量")
    class ViewCount {

        @Test
        @DisplayName("增加浏览量应调用仓储层 updateViewCount")
        void shouldIncrementViewCount() {
            // when
            articleApplicationService.incrementViewCount(1L);

            // then
            verify(articleRepository).updateViewCount(1L);
        }

        @Test
        @DisplayName("文章不存在时视图计数不应抛出异常（静默处理）")
        void shouldNotThrowWhenArticleNotFound() {
            // 这里仓储层 updateViewCount 是直接 SQL 更新，不存在也不抛异常
            // 验证不会抛出任何异常即可
            assertDoesNotThrow(() -> articleApplicationService.incrementViewCount(999L));
        }
    }

    // ======================== 点赞 ========================

    @Nested
    @DisplayName("点赞/取消点赞")
    class Like {

        @Test
        @DisplayName("新增点赞应增加 likeCount")
        void shouldIncreaseLikeCount() {
            // given
            when(articleRepository.findById(1L)).thenReturn(draftArticle);
            when(articleRepository.toggleLike(1L, 100L)).thenReturn(true);

            // when
            articleApplicationService.toggleLike(1L, 100L);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            assertEquals(1L, articleCaptor.getValue().getLikeCount());
        }

        @Test
        @DisplayName("取消点赞应减少 likeCount")
        void shouldDecreaseLikeCount() {
            // given
            when(articleRepository.findById(2L)).thenReturn(publishedArticle);
            when(articleRepository.toggleLike(2L, 100L)).thenReturn(false);

            // when
            articleApplicationService.toggleLike(2L, 100L);

            // then
            verify(articleRepository).save(articleCaptor.capture());
            assertEquals(9L, articleCaptor.getValue().getLikeCount());
        }

        @Test
        @DisplayName("点赞不存在的文章应抛出异常")
        void shouldThrowWhenArticleNotFound() {
            // given
            when(articleRepository.findById(999L)).thenReturn(null);

            // when & then
            assertThrows(BizException.class,
                    () -> articleApplicationService.toggleLike(999L, 100L));
        }
    }

    // ======================== 最新文章 ========================

    @Nested
    @DisplayName("最新文章")
    class LatestArticles {

        @Test
        @DisplayName("应返回指定数量的最新文章")
        void shouldReturnLatestArticles() {
            // given
            when(articleRepository.findLatest(3)).thenReturn(List.of(publishedArticle, draftArticle, offlineArticle));

            // when
            List<ArticleVO> result = articleApplicationService.getLatestArticles(3);

            // then
            assertEquals(3, result.size());
            assertEquals("已发布文章", result.get(0).getTitle());
            assertEquals("草稿文章", result.get(1).getTitle());
            assertEquals("下架文章", result.get(2).getTitle());
            verify(articleRepository).findLatest(3);
        }
    }

    // ======================== 批量操作 ========================

    @Nested
    @DisplayName("批量操作")
    class BatchOperation {

        @Test
        @DisplayName("批量删除应只删除非已发布文章")
        void batchDeleteShouldOnlyDeleteNonPublished() {
            // given
            BatchArticleRequest request = new BatchArticleRequest();
            request.setIds(List.of(1L, 2L, 3L));
            request.setAction("delete");

            when(articleRepository.findById(1L)).thenReturn(draftArticle);
            when(articleRepository.findById(2L)).thenReturn(publishedArticle);
            when(articleRepository.findById(3L)).thenReturn(offlineArticle);

            // when
            articleApplicationService.batchOperation(request);

            // then
            verify(articleRepository).delete(1L);
            verify(articleRepository, never()).delete(2L);
            verify(articleRepository).delete(3L);
        }

        @Test
        @DisplayName("批量发布应发布所有文章")
        void batchPublishShouldPublishAll() {
            // given
            BatchArticleRequest request = new BatchArticleRequest();
            request.setIds(List.of(1L, 2L));
            request.setAction("publish");

            when(articleRepository.findById(1L)).thenReturn(draftArticle);
            when(articleRepository.findById(2L)).thenReturn(publishedArticle);

            // when
            articleApplicationService.batchOperation(request);

            // then
            verify(articleRepository, times(2)).save(any(Article.class));
        }

        @Test
        @DisplayName("空 ID 列表应直接返回，不调用仓储")
        void emptyIdsShouldDoNothing() {
            // given
            BatchArticleRequest request = new BatchArticleRequest();
            request.setIds(List.of());
            request.setAction("delete");

            // when
            articleApplicationService.batchOperation(request);

            // then
            verifyNoInteractions(articleRepository);
        }

        @Test
        @DisplayName("不支持的 action 应抛出异常")
        void unsupportedActionShouldThrow() {
            // given
            BatchArticleRequest request = new BatchArticleRequest();
            request.setIds(List.of(1L));
            request.setAction("unknown");

            // when & then
            assertThrows(IllegalArgumentException.class,
                    () -> articleApplicationService.batchOperation(request));
        }

        @Test
        @DisplayName("批量删除时，文章不存在应静默跳过")
        void batchDeleteShouldSkipNonExistentArticles() {
            // given
            BatchArticleRequest request = new BatchArticleRequest();
            request.setIds(List.of(1L, 999L));
            request.setAction("delete");

            when(articleRepository.findById(1L)).thenReturn(draftArticle);
            when(articleRepository.findById(999L)).thenReturn(null);

            // when
            articleApplicationService.batchOperation(request);

            // then
            verify(articleRepository).delete(1L);
            verify(articleRepository, never()).delete(999L);
        }
    }
}