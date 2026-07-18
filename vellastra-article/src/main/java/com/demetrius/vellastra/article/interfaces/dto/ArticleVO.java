package com.demetrius.vellastra.article.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: ArticleVO</p>
 * <p>Description: 文章视图对象，供前台展示和管理后台使用</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 * <p>
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
@Data
public class ArticleVO {

    /** 文章ID */
    private Long id;
    /** 文章标题 */
    private String title;
    /** 文章摘要 */
    private String summary;
    /** 文章正文（Markdown格式） */
    private String content;
    /** 文章正文（HTML渲染） */
    private String contentHtml;
    /** 封面图片URL */
    private String coverImage;
    /** 分类ID */
    private Long categoryId;
    /** 分类名称 */
    private String categoryName;
    /** 文章状态：0-草稿，1-待审核，2-已发布，3-下架 */
    private Integer status;
    /** 作者ID */
    private Long authorId;
    /** 作者昵称 */
    private String authorName;
    /** 浏览量 */
    private Long viewCount;
    /** 点赞数 */
    private Long likeCount;
    /** 是否置顶：0-否，1-是 */
    private Integer isTop;
    /** 评论数 */
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
}