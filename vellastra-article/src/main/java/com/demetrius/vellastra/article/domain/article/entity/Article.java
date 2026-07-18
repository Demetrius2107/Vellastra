package com.demetrius.vellastra.article.domain.article.entity;

import com.demetrius.vellastra.article.domain.article.valueobject.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: Article</p>
 * <p>Description: 文章领域实体</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    /** ID */
    @NotNull
    private Long id;

    /** 文章标题 */
    @NotBlank
    private String title;

    /** 正文（Markdown） */
    @NotBlank
    private String content;

    /** 摘要 */
    private String summary;

    /** 正文（HTML渲染） */
    private String contentHtml;

    /** 封面图片URL */
    private String coverImage;

    /** 分类ID */
    @NotNull
    private Long categoryId;

    /**
     * 状态：0-草稿，1-待审核，2-已发布，3-下架
     */
    @NotNull
    private Integer status;

    /** 是否置顶：0-否，1-是 */
    private Integer isTop;

    /** 作者ID */
    @NotNull
    private Long authorId;

    /** 浏览量，默认0 */
    private Long viewCount;

    /** 点赞数，默认0 */
    private Long likeCount;

    /** 评论数，默认0 */
    private Integer commentCount;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** SEO标题 */
    private String seoTitle;

    /** SEO描述 */
    private String seoDescription;

    /** SEO关键词 */
    private String seoKeywords;

    /** 是否已发布 */
    public boolean isPublished() {
        return this.status == ArticleStatus.PUBLISHED.getCode();
    }

    /** 发布（草稿/待审核 → 已发布） */
    public void publish() {
        this.status = ArticleStatus.PUBLISHED.getCode();
        this.updateTime();
    }

    /** 设为草稿 */
    public void draft() {
        this.status = ArticleStatus.DRAFT.getCode();
        this.updateTime();
    }

    /** 初始化创建时间 */
    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /** 更新时间 */
    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}