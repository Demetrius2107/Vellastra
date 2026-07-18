package com.demetrius.vellastra.article.domain.article.entity;

import com.demetrius.vellastra.article.domain.article.valueobject.ArticleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章领域实体单元测试
 */
@DisplayName("Article 领域实体")
class ArticleTest {

    private Article article;

    @BeforeEach
    void setUp() {
        article = Article.builder()
                .id(1L)
                .title("测试文章")
                .content("测试内容")
                .summary("测试摘要")
                .categoryId(1L)
                .status(ArticleStatus.DRAFT.getCode())
                .authorId(100L)
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0)
                .isTop(0)
                .build();
    }

    // ======================== 创建 / 初始化 ========================

    @Test
    @DisplayName("构建文章实体应正确设置所有字段")
    void shouldBuildArticleWithAllFields() {
        assertNotNull(article);
        assertEquals(1L, article.getId());
        assertEquals("测试文章", article.getTitle());
        assertEquals("测试内容", article.getContent());
        assertEquals("测试摘要", article.getSummary());
        assertEquals(1L, article.getCategoryId());
        assertEquals(ArticleStatus.DRAFT.getCode(), article.getStatus());
        assertEquals(100L, article.getAuthorId());
        assertEquals(0L, article.getViewCount());
        assertEquals(0L, article.getLikeCount());
        assertEquals(0, article.getCommentCount());
        assertEquals(0, article.getIsTop());
    }

    @Test
    @DisplayName("initCreateTime 应设置创建时间和更新时间")
    void initCreateTimeShouldSetTimes() {
        assertNull(article.getCreateTime());
        assertNull(article.getUpdateTime());

        article.initCreateTime();

        assertNotNull(article.getCreateTime());
        assertNotNull(article.getUpdateTime());
        assertTrue(article.getCreateTime().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(article.getUpdateTime().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("updateTime 应更新更新时间")
    void updateTimeShouldUpdate() {
        article.initCreateTime();
        LocalDateTime oldUpdateTime = article.getUpdateTime();

        // 等待极短时间后更新
        article.updateTime();

        assertNotNull(article.getUpdateTime());
        assertTrue(article.getUpdateTime().isAfter(oldUpdateTime)
                || article.getUpdateTime().isEqual(oldUpdateTime));
    }

    // ======================== 状态判断 ========================

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3})
    @DisplayName("非已发布状态 isPublished 应返回 false")
    void isPublishedShouldReturnFalseForNonPublishedStatus(int statusCode) {
        article.setStatus(statusCode);
        assertFalse(article.isPublished());
    }

    @Test
    @DisplayName("已发布状态 isPublished 应返回 true")
    void isPublishedShouldReturnTrueWhenPublished() {
        article.setStatus(ArticleStatus.PUBLISHED.getCode());
        assertTrue(article.isPublished());
    }

    // ======================== 状态变更 ========================

    @Test
    @DisplayName("publish 应将状态设为已发布并更新时间")
    void publishShouldSetStatusToPublished() {
        article.initCreateTime();
        LocalDateTime beforePublish = LocalDateTime.now();

        article.publish();

        assertEquals(ArticleStatus.PUBLISHED.getCode(), article.getStatus());
        assertNotNull(article.getUpdateTime());
        assertTrue(article.getUpdateTime().isAfter(beforePublish)
                || article.getUpdateTime().isEqual(beforePublish));
    }

    @Test
    @DisplayName("draft 应将状态设为草稿并更新时间")
    void draftShouldSetStatusToDraft() {
        article.setStatus(ArticleStatus.PUBLISHED.getCode());
        article.initCreateTime();

        article.draft();

        assertEquals(ArticleStatus.DRAFT.getCode(), article.getStatus());
        assertNotNull(article.getUpdateTime());
    }

    @Test
    @DisplayName("从草稿→发布→下架→草稿 状态流转应正确")
    void shouldTransitionThroughAllStatuses() {
        // 初始：草稿
        assertEquals(ArticleStatus.DRAFT.getCode(), article.getStatus());

        // 发布
        article.publish();
        assertEquals(ArticleStatus.PUBLISHED.getCode(), article.getStatus());

        // 设为草稿
        article.draft();
        assertEquals(ArticleStatus.DRAFT.getCode(), article.getStatus());

        // 再次发布
        article.publish();
        assertEquals(ArticleStatus.PUBLISHED.getCode(), article.getStatus());
    }

    // ======================== 置顶标记 ========================

    @Test
    @DisplayName("设置 isTop 应正确更新")
    void setIsTopShouldWork() {
        assertEquals(0, article.getIsTop());

        article.setIsTop(1);
        assertEquals(1, article.getIsTop());

        article.setIsTop(0);
        assertEquals(0, article.getIsTop());
    }

    // ======================== 计数更新 ========================

    @Test
    @DisplayName("viewCount 和 likeCount 应正确设置初始值")
    void countsShouldBeInitialized() {
        assertEquals(0L, article.getViewCount());
        assertEquals(0L, article.getLikeCount());
        assertEquals(0, article.getCommentCount());
    }

    // ======================== 空参构造 + Builder ========================

    @Test
    @DisplayName("NoArgsConstructor 应创建空对象")
    void noArgsConstructorShouldCreateEmpty() {
        Article emptyArticle = new Article();
        assertNotNull(emptyArticle);
    }

    @Test
    @DisplayName("AllArgsConstructor 应设置所有字段")
    void allArgsConstructorShouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Article fullArticle = new Article(
                2L, "标题", "内容", "摘要", "html", "cover.jpg",
                1L, 2, 1, 200L, 10L, 5L, 5, now, now, now,
                "seo标题", "seo描述", "seo关键词"
        );

        assertEquals(2L, fullArticle.getId());
        assertEquals("标题", fullArticle.getTitle());
        assertEquals("seo关键词", fullArticle.getSeoKeywords());
    }
}