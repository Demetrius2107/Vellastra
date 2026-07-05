package com.demetrius.blog.article.interfaces.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: BatchArticleRequest</p>
 * <p>Description: 批量操作请求 DTO</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年07月05日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
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
