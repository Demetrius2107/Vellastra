package com.demetrius.vellastra.article.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: CreateArticleRequest</p>
 * <p>Description: 创建文章请求 DTO</p>
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
public class CreateArticleRequest {

    /** 标题*/
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 内容*/
    @NotBlank(message = "内容不能为空")
    private String content;

    /** 总结*/
    private String summary;

    /** 展示图*/
    private String coverImage;

    /** 分类ID*/
    private Long categoryId;

    /** 状态 0 草稿 1 发布 2 删除 3下架*/
    private Integer status;

    /** 标签*/
    private String tags;
}
