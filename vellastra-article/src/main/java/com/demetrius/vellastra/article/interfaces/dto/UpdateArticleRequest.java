package com.demetrius.vellastra.article.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: UpdateArticleRequest</p>
 * <p>Description: 更新文章请求 DTO</p>
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
