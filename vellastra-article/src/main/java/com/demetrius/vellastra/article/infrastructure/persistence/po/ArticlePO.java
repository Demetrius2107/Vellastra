package com.demetrius.vellastra.article.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: ArticlePO</p>
 * <p>Description: 文章持久化对象，与 blog_article 表 1:1 对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-17
 * <p>
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@TableName("blog_article")
public class ArticlePO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 正文（Markdown）
     */
    private String content;

    /**
     * 正文（HTML渲染）
     */
    private String contentHtml;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 状态：0草稿 1待审核 2已发布 3下架
     */
    private Integer status;

    /**
     * 是否置顶：0否 1是
     */
    private Integer isTop;

    /**
     * 浏览量，默认0
     */
    private Long viewCount;

    /**
     * 点赞数，默认0
     */
    private Long likeCount;

    /**
     * 评论数，默认0
     */
    private Integer commentCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * SEO标题
     */
    private String seoTitle;

    /**
     * SEO描述
     */
    private String seoDescription;

    /**
     * SEO关键词
     */
    private String seoKeywords;

    /**
     * 作者用户ID
     */
    private Long authorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}