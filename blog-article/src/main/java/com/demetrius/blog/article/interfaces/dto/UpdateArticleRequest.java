package com.demetrius.blog.article.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateArticleRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 汇总
     */
    private String summary;

    /**
     * 封面图
     */
    private String coverImage;

    /**
     * 分类
     */
    private Long categoryId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 标签
     */
    private String tags;
}
