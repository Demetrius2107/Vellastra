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
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    /**
     * ID
     */
    @NotNull
    private Long id;

    /**
     * 文章标题（核心必填，字符串非空）
     */
    @NotBlank
    private String title;

    /**
     * 内容（核心必填，字符串非空）
     */
    @NotBlank
    private String content;

    /**
     * 总结（非必填，允许为空）
     */
    private String summary;

    /**
     * 图片（非必填，允许为空）
     */
    private String coverImage;

    /**
     * 分类（业务必填，文章必须归属分类）
     */
    @NotNull
    private Long categoryId;

    /**
     * 状态（必填，默认有草稿/发布状态）
     * 0 草稿
     * 1 发布
     * 2 删除
     * 3 下架
     */
    @NotNull
    private Integer status;

    /**
     * 标签（非必填，允许为空）
     */
    private String tags;

    /**
     * 作者id（业务必填）
     */
    @NotNull
    private Long authorId;

    /**
     * 浏览量（默认0，非必填）
     */
    private Long viewCount;

    /**
     * 点赞数（默认0，非必填）
     */
    private Long likeCount;

    /**
     * 评论数（默认0，非必填）
     */
    private Integer commentCount;

    /**
     * 是否热门（非必填，默认0）
     */
    private Integer isTop;

    /**
     * 发布时间（发布时赋值，非必填）
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间（代码自动初始化，无需前端校验）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（代码自动初始化，无需前端校验）
     */
    private LocalDateTime updateTime;

    /**
     * 是否发布
     */
    public boolean isPublished() {
        return this.status == ArticleStatus.PUBLISHED.getCode();
    }

    /**
     * 发布
     */
    public void publish() {
        this.status = ArticleStatus.PUBLISHED.getCode();
        this.updateTime();
    }

    /**
     * 草稿
     */
    public void draft() {
        this.status = ArticleStatus.DRAFT.getCode();
        this.updateTime();
    }

    /**
     * 初始化时间
     *
     */
    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新时间
     *
     */
    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}