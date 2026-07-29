package com.demetrius.vellastra.article.interfaces.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>Title: DashboardVO</p>
 * <p>Description: 数据仪表盘视图对象</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
public class DashboardVO {

    /** 总览统计 */
    private OverviewVO overview;
    /** 趋势数据 */
    private List<TrendVO> trend;
    /** 热门文章TOP10 */
    private List<ArticleStatVO> hotArticles;
    /** 分类统计 */
    private List<CategoryStatVO> categoryStats;

    @Data
    public static class OverviewVO {
        /** 文章总数 */
        private Long articleCount;
        /** 用户总数 */
        private Long userCount;
        /** 评论总数 */
        private Long commentCount;
        /** 总浏览量 */
        private Long totalViewCount;
        /** 总点赞数 */
        private Long totalLikeCount;
        /** 标签总数 */
        private Long tagCount;
    }

    @Data
    public static class TrendVO {
        /** 日期 */
        private LocalDate date;
        /** 发文量 */
        private Long articleCount;
        /** 访问量 */
        private Long viewCount;
    }

    @Data
    public static class ArticleStatVO {
        /** 文章ID */
        private Long id;
        /** 标题 */
        private String title;
        /** 浏览量 */
        private Long viewCount;
        /** 点赞数 */
        private Long likeCount;
    }

    @Data
    public static class CategoryStatVO {
        /** 分类ID */
        private Long id;
        /** 分类名称 */
        private String name;
        /** 文章数量 */
        private Long articleCount;
    }
}