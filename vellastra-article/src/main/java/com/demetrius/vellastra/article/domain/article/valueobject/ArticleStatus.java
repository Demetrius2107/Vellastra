package com.demetrius.vellastra.article.domain.article.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Title: ArticleStatus</p>
 * <p>Description: 文章状态枚举，与 blog_article 表 status 列一致</p>
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
public enum ArticleStatus {

    DRAFT(0, "草稿"),
    REVIEWING(1, "待审核"),
    PUBLISHED(2, "已发布"),
    OFFLINE(3, "下架");

    private final int code;
    private final String desc;

    public static ArticleStatus of(int code) {
        for (ArticleStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return DRAFT;
    }
}