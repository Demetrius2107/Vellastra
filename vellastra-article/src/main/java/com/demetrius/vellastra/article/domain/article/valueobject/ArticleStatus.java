package com.demetrius.vellastra.article.domain.article.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Title: ArticleStatus</p>
 * <p>Description: 文章状态枚举，与 blog_article 表 status 列一致</p>
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