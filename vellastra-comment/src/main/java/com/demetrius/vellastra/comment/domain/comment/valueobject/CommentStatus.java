package com.demetrius.vellastra.comment.domain.comment.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Title: CommentStatus</p>
 * <p>Description: 评论状态枚举（待审核/已通过/已拒绝）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Getter
@AllArgsConstructor
public enum CommentStatus {

    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝");

    private final int code;
    private final String desc;

    public static CommentStatus of(int code) {
        for (CommentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
}
