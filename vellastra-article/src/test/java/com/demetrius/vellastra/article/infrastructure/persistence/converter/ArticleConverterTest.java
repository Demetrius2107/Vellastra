package com.demetrius.vellastra.article.infrastructure.persistence.converter;

import com.demetrius.vellastra.article.domain.article.entity.Article;
import com.demetrius.vellastra.article.domain.article.valueobject.ArticleStatus;
import com.demetrius.vellastra.article.infrastructure.persistence.po.ArticlePO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章对象转换器单元测试
 */
@DisplayName("ArticleConverter 转换器")
class ArticleConverterTest {

    private ArticleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ArticleConverter();
    }

    // ======================== toDomain ========================

    @Test
    @DisplayName("toDomain 应正确转换 PO 为领域对象（全字段）")
    void toDomainShouldConvertAllFields() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 17, 10, 0, 0);
        ArticlePO po = buildFullPO(now);

        Article domain = converter.toDomain(po);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals("测试文章标题", domain.getTitle());
        assertEquals("这是一段测试摘要", domain.getSummary());
        assertEquals("## Markdown 正文", domain.getContent());
        assertEquals("<h2>Markdown 正文</h2>", domain.getContentHtml());
        assertEquals("https://example.com/cover.jpg", domain.getCoverImage());
        assertEquals(10L, domain.getCategoryId());
        assertEquals(ArticleStatus.PUBLISHED.getCode(), domain.getStatus());
        assertEquals(1, domain.getIsTop());
        assertEquals(100L, domain.getAuthorId());
        assertEquals(999L, domain.getViewCount());
        assertEquals(50L, domain.getLikeCount());
        assertEquals(12, domain.getCommentCount());
        assertEquals(now, domain.getPublishTime());
        assertEquals(now, domain.getCreateTime());
        assertEquals(now, domain.getUpdateTime());
        assertEquals("SEO标题", domain.getSeoTitle());
        assertEquals("SEO描述", domain.getSeoDescription());
        assertEquals("关键词1,关键词2", domain.getSeoKeywords());
    }

    @Test
    @DisplayName("toDomain(null) 应返回 null")
    void toDomainNullShouldReturnNull() {
        assertNull(converter.toDomain(null));
    }

    @Test
    @DisplayName("toDomain 应正确处理空字段")
    void toDomainShouldHandleEmptyFields() {
        ArticlePO po = new ArticlePO();
        po.setId(1L);
        po.setTitle("标题");
        po.setContent("内容");
        po.setCategoryId(1L);
        po.setStatus(0);
        po.setAuthorId(1L);

        Article domain = converter.toDomain(po);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals("标题", domain.getTitle());
        assertNull(domain.getSummary());
        assertNull(domain.getContentHtml());
        assertNull(domain.getCoverImage());
        assertNull(domain.getViewCount());
        assertNull(domain.getLikeCount());
        assertNull(domain.getPublishTime());
        assertNull(domain.getCreateTime());
    }

    // ======================== toPO ========================

    @Test
    @DisplayName("toPO 应正确转换领域对象为 PO（全字段）")
    void toPOShouldConvertAllFields() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 17, 10, 0, 0);
        Article domain = buildFullDomain(now);

        ArticlePO po = converter.toPO(domain);

        assertNotNull(po);
        assertEquals(1L, po.getId());
        assertEquals("测试文章标题", po.getTitle());
        assertEquals("这是一段测试摘要", po.getSummary());
        assertEquals("## Markdown 正文", po.getContent());
        assertEquals("<h2>Markdown 正文</h2>", po.getContentHtml());
        assertEquals("https://example.com/cover.jpg", po.getCoverImage());
        assertEquals(10L, po.getCategoryId());
        assertEquals(ArticleStatus.PUBLISHED.getCode(), po.getStatus());
        assertEquals(1, po.getIsTop());
        assertEquals(100L, po.getAuthorId());
        assertEquals(999L, po.getViewCount());
        assertEquals(50L, po.getLikeCount());
        assertEquals(12, po.getCommentCount());
        assertEquals(now, po.getPublishTime());
        assertEquals(now, po.getCreateTime());
        assertEquals(now, po.getUpdateTime());
        assertEquals("SEO标题", po.getSeoTitle());
        assertEquals("SEO描述", po.getSeoDescription());
        assertEquals("关键词1,关键词2", po.getSeoKeywords());
    }

    @Test
    @DisplayName("toPO(null) 应返回 null")
    void toPONullShouldReturnNull() {
        assertNull(converter.toPO(null));
    }

    @Test
    @DisplayName("toPO 应正确处理空字段")
    void toPOShouldHandleEmptyFields() {
        Article domain = Article.builder()
                .id(1L)
                .title("标题")
                .content("内容")
                .categoryId(1L)
                .status(0)
                .authorId(1L)
                .build();

        ArticlePO po = converter.toPO(domain);

        assertNotNull(po);
        assertEquals(1L, po.getId());
        assertEquals("标题", po.getTitle());
        assertNull(po.getSummary());
        assertNull(po.getContentHtml());
        assertNull(po.getCoverImage());
        assertNull(po.getViewCount());
        assertNull(po.getPublishTime());
    }

    // ======================== 双向转换一致性 ========================

    @Test
    @DisplayName("PO → Domain → PO 应保持字段一致")
    void roundTripPOToDomainToPOShouldBeConsistent() {
        LocalDateTime now = LocalDateTime.now();
        ArticlePO originalPo = buildFullPO(now);

        Article domain = converter.toDomain(originalPo);
        ArticlePO convertedPo = converter.toPO(domain);

        assertNotNull(domain);
        assertNotNull(convertedPo);
        assertEquals(originalPo.getId(), convertedPo.getId());
        assertEquals(originalPo.getTitle(), convertedPo.getTitle());
        assertEquals(originalPo.getContent(), convertedPo.getContent());
        assertEquals(originalPo.getContentHtml(), convertedPo.getContentHtml());
        assertEquals(originalPo.getSummary(), convertedPo.getSummary());
        assertEquals(originalPo.getCoverImage(), convertedPo.getCoverImage());
        assertEquals(originalPo.getCategoryId(), convertedPo.getCategoryId());
        assertEquals(originalPo.getStatus(), convertedPo.getStatus());
        assertEquals(originalPo.getIsTop(), convertedPo.getIsTop());
        assertEquals(originalPo.getAuthorId(), convertedPo.getAuthorId());
        assertEquals(originalPo.getViewCount(), convertedPo.getViewCount());
        assertEquals(originalPo.getLikeCount(), convertedPo.getLikeCount());
        assertEquals(originalPo.getCommentCount(), convertedPo.getCommentCount());
        assertEquals(originalPo.getPublishTime(), convertedPo.getPublishTime());
        assertEquals(originalPo.getCreateTime(), convertedPo.getCreateTime());
        assertEquals(originalPo.getUpdateTime(), convertedPo.getUpdateTime());
        assertEquals(originalPo.getSeoTitle(), convertedPo.getSeoTitle());
        assertEquals(originalPo.getSeoDescription(), convertedPo.getSeoDescription());
        assertEquals(originalPo.getSeoKeywords(), convertedPo.getSeoKeywords());
    }

    @Test
    @DisplayName("Domain → PO → Domain 应保持字段一致")
    void roundTripDomainToPOToDomainShouldBeConsistent() {
        LocalDateTime now = LocalDateTime.now();
        Article originalDomain = buildFullDomain(now);

        ArticlePO po = converter.toPO(originalDomain);
        Article convertedDomain = converter.toDomain(po);

        assertNotNull(po);
        assertNotNull(convertedDomain);
        assertEquals(originalDomain.getId(), convertedDomain.getId());
        assertEquals(originalDomain.getTitle(), convertedDomain.getTitle());
        assertEquals(originalDomain.getContent(), convertedDomain.getContent());
        assertEquals(originalDomain.getContentHtml(), convertedDomain.getContentHtml());
        assertEquals(originalDomain.getSummary(), convertedDomain.getSummary());
        assertEquals(originalDomain.getCoverImage(), convertedDomain.getCoverImage());
        assertEquals(originalDomain.getCategoryId(), convertedDomain.getCategoryId());
        assertEquals(originalDomain.getStatus(), convertedDomain.getStatus());
        assertEquals(originalDomain.getIsTop(), convertedDomain.getIsTop());
        assertEquals(originalDomain.getAuthorId(), convertedDomain.getAuthorId());
        assertEquals(originalDomain.getViewCount(), convertedDomain.getViewCount());
        assertEquals(originalDomain.getLikeCount(), convertedDomain.getLikeCount());
        assertEquals(originalDomain.getCommentCount(), convertedDomain.getCommentCount());
        assertEquals(originalDomain.getPublishTime(), convertedDomain.getPublishTime());
        assertEquals(originalDomain.getCreateTime(), convertedDomain.getCreateTime());
        assertEquals(originalDomain.getUpdateTime(), convertedDomain.getUpdateTime());
        assertEquals(originalDomain.getSeoTitle(), convertedDomain.getSeoTitle());
        assertEquals(originalDomain.getSeoDescription(), convertedDomain.getSeoDescription());
        assertEquals(originalDomain.getSeoKeywords(), convertedDomain.getSeoKeywords());
    }

    // ======================== 辅助方法 ========================

    private ArticlePO buildFullPO(LocalDateTime time) {
        ArticlePO po = new ArticlePO();
        po.setId(1L);
        po.setTitle("测试文章标题");
        po.setSummary("这是一段测试摘要");
        po.setContent("## Markdown 正文");
        po.setContentHtml("<h2>Markdown 正文</h2>");
        po.setCoverImage("https://example.com/cover.jpg");
        po.setCategoryId(10L);
        po.setStatus(ArticleStatus.PUBLISHED.getCode());
        po.setIsTop(1);
        po.setAuthorId(100L);
        po.setViewCount(999L);
        po.setLikeCount(50L);
        po.setCommentCount(12);
        po.setPublishTime(time);
        po.setCreateTime(time);
        po.setUpdateTime(time);
        po.setSeoTitle("SEO标题");
        po.setSeoDescription("SEO描述");
        po.setSeoKeywords("关键词1,关键词2");
        return po;
    }

    private Article buildFullDomain(LocalDateTime time) {
        return Article.builder()
                .id(1L)
                .title("测试文章标题")
                .summary("这是一段测试摘要")
                .content("## Markdown 正文")
                .contentHtml("<h2>Markdown 正文</h2>")
                .coverImage("https://example.com/cover.jpg")
                .categoryId(10L)
                .status(ArticleStatus.PUBLISHED.getCode())
                .isTop(1)
                .authorId(100L)
                .viewCount(999L)
                .likeCount(50L)
                .commentCount(12)
                .publishTime(time)
                .createTime(time)
                .updateTime(time)
                .seoTitle("SEO标题")
                .seoDescription("SEO描述")
                .seoKeywords("关键词1,关键词2")
                .build();
    }
}