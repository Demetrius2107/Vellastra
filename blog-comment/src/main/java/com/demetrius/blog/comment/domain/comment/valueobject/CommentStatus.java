package com.demetrius.blog.comment.domain.comment.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Title: CommentStatus</p>
 * <p>Description: 评论状态枚举（待审核/已通过/已拒绝）</p>
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
