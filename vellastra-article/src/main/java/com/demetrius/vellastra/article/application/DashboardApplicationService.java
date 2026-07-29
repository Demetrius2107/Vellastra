package com.demetrius.vellastra.article.application;

import com.demetrius.vellastra.article.infrastructure.persistence.mapper.ArticleMapper;
import com.demetrius.vellastra.article.infrastructure.persistence.po.ArticlePO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.article.interfaces.dto.DashboardVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: DashboardApplicationService</p>
 * <p>Description: 仪表盘应用服务，提供多表聚合统计数据</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class DashboardApplicationService {

    private final ArticleMapper articleMapper;

    public DashboardApplicationService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    /**
     * 获取仪表盘全量数据
     */
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setOverview(getOverview());
        vo.setTrend(getTrend());
        vo.setHotArticles(getHotArticles());
        vo.setCategoryStats(getCategoryStats());
        return vo;
    }

    /**
     * 总览统计
     */
    private DashboardVO.OverviewVO getOverview() {
        DashboardVO.OverviewVO vo = new DashboardVO.OverviewVO();
        vo.setArticleCount(articleMapper.selectCount(null));
        vo.setTotalViewCount(getSumViewCount());
        vo.setTotalLikeCount(getSumLikeCount());
        return vo;
    }

    private Long getSumViewCount() {
        List<ArticlePO> all = articleMapper.selectList(
                new LambdaQueryWrapper<ArticlePO>().select(ArticlePO::getViewCount));
        return all.stream().mapToLong(a -> a.getViewCount() != null ? a.getViewCount() : 0).sum();
    }

    private Long getSumLikeCount() {
        List<ArticlePO> all = articleMapper.selectList(
                new LambdaQueryWrapper<ArticlePO>().select(ArticlePO::getLikeCount));
        return all.stream().mapToLong(a -> a.getLikeCount() != null ? a.getLikeCount() : 0).sum();
    }

    /**
     * 近7天趋势
     */
    private List<DashboardVO.TrendVO> getTrend() {
        LocalDate today = LocalDate.now();
        return List.of(); // 简化实现，后续补充按天GROUP BY
    }

    /**
     * 热门文章TOP10
     */
    private List<DashboardVO.ArticleStatVO> getHotArticles() {
        return articleMapper.selectList(
                new LambdaQueryWrapper<ArticlePO>()
                        .orderByDesc(ArticlePO::getViewCount)
                        .last("LIMIT 10")
        ).stream().map(a -> {
            DashboardVO.ArticleStatVO vo = new DashboardVO.ArticleStatVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setViewCount(a.getViewCount());
            vo.setLikeCount(a.getLikeCount());
            return vo;
        }).toList();
    }

    /**
     * 分类统计
     */
    private List<DashboardVO.CategoryStatVO> getCategoryStats() {
        return List.of(); // 简化实现，后续补充分类聚合
    }
}