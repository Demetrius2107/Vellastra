package com.demetrius.vellastra.comment.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>Title: ReplyCommentRequest</p>
 * <p>Description: 回复评论请求</p>
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
public class ReplyCommentRequest {

    /** 文章ID */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /** 父评论ID */
    @NotNull(message = "父评论ID不能为空")
    private Long parentId;

    /** 被回复评论ID */
    @NotNull(message = "被回复评论ID不能为空")
    private Long replyToId;

    /** 回复内容 */
    @NotBlank(message = "回复内容不能为空")
    private String content;
}
