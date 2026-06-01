package com.demetrius.blog.article.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @description: 发布文章请求dto
 * @author: wanqiu
 * @date: 2026-05-17 12:53:05
 * @version: 1.0
 *
 *
 * */
public class PublishAtricleRequest {

    /**
     * 标题
     * */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     *  内容
     *  */
    @NotBlank(message = "内容不能为空")
    private String content;

    /** 总结*/
    private String summary;

    /** 展示图*/
    private String coverImage;

    /** 分类ID*/
    private Long categoryId;

    /** 状态 草稿 发布 删除 下架*/
    private Integer status;

    /** 标签*/
    private String tags;

}
