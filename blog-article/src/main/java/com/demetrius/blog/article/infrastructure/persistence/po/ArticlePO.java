package com.demetrius.blog.article.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: ArticlePO</p>
 * <p>Description: 文章持久化对象</p>
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
@TableName("t_article")
public class ArticlePO {

    /** 文章ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** 正文（Markdown） */
    private String content;

    /** 正文（HTML渲染） */
    private String contentHtml;

    /** 封面图URL */
    private String coverImage;

    /** 分类ID */
    private Long categoryId;

    /** 状态：0草稿 1已发布 2已下架 */
    private Integer status;

    /** 是否置顶：0否 1是 */
    private Integer isTop;

    /** 是否原创：0转载 1原创 */
    private Integer isOriginal;

    /** 原文链接（转载时） */
    private String sourceUrl;

    /** 标签（逗号分隔） */
    private String tags;

    /** 作者ID */
    private Long authorId;

    /** 浏览次数 */
    private Long viewCount;

    /** 点赞数 */
    private Long likeCount;

    /** 评论数 */
    private Integer commentCount;

    /** 字数统计 */
    private Integer wordCount;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
