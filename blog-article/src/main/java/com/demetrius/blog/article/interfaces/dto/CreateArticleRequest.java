package com.demetrius.blog.article.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
