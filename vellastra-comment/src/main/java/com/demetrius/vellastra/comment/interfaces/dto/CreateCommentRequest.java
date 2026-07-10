package com.demetrius.vellastra.comment.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>Title: CreateCommentRequest</p>
 * <p>Description: 创建评论请求</p>
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
public class CreateCommentRequest {

    /** 文章ID */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
