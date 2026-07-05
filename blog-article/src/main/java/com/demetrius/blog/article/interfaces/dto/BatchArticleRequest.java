package com.demetrius.blog.article.interfaces.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量操作请求
 */
@Data
public class BatchArticleRequest {

    /** 文章ID列表 */
    @NotEmpty(message = "文章ID列表不能为空")
    private List<Long> ids;

    /** 操作类型：delete / publish */
    @NotEmpty(message = "操作类型不能为空")
    private String action;
}
