package com.demetrius.blog.article.domain.article.entity;

import com.demetrius.blog.article.domain.article.valueobject.ArticleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    /** ID*/
    private Long id;

    /** 文章标题*/
    private String title;

    /** 内容*/
    private String content;

    /** 总结*/
    private String summary;

    /** 图片*/
    private String coverImage;


    /*** 分类*/
    private Long categoryId;

    /** 状态*/
    private Integer status;

    /** 标签*/
    private String tags;

    /** 作者id*/
    private Long authorId;

    /** 浏览量*/
    private Long viewCount;

    /** 点赞数*/
    private Long likeCount;

    /** 评论数*/
    private Integer commentCount;

    /** 是否热门*/
    private Integer isTop;

    /** 发布时间*/
    private LocalDateTime publishTime;

    /** 创建时间*/
    private LocalDateTime createTime;

    /** 更新时间*/
    private LocalDateTime updateTime;

    /** 是否发布*/
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
     * 置顶
     */
    public void draft() {
        this.status = ArticleStatus.DRAFT.getCode();
        this.updateTime();
    }

    /**
     * 初始化时间
     * */
    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新时间
     * */
    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}
