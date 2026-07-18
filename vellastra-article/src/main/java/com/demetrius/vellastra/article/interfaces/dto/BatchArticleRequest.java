package com.demetrius.vellastra.article.interfaces.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: BatchArticleRequest</p>
 * <p>Description: 批量操作请求 DTO</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-05
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
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
