package com.demetrius.vellastra.analytics.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.analytics.infrastructure.mapper.AnalyticsSummaryMapper;
import com.demetrius.vellastra.analytics.infrastructure.po.AnalyticsSummaryPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyticsService {

    private final JdbcTemplate jdbcTemplate;
    private final AnalyticsSummaryMapper summaryMapper;

    public AnalyticsService(JdbcTemplate jdbcTemplate, AnalyticsSummaryMapper summaryMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.summaryMapper = summaryMapper;
    }

    // ===================== 实时统计 =====================

    public Map<String, Object> overview() {
        Long articleCount = queryLong("SELECT COUNT(*) FROM blog_article");
        Long userCount = queryLong("SELECT COUNT(*) FROM t_user");
        Long commentCount = queryLong("SELECT COUNT(*) FROM t_comment");
        Long totalViews = queryLong("SELECT COALESCE(SUM(view_count),0) FROM blog_article");
        Long totalLikes = queryLong("SELECT COALESCE(SUM(like_count),0) FROM blog_article");
        Long categoryCount = queryLong("SELECT COUNT(*) FROM t_category");
        Long tagCount = queryLong("SELECT COUNT(*) FROM t_tag");
        return Map.of(
                "articleCount", articleCount, "userCount", userCount,
                "commentCount", commentCount, "totalViews", totalViews,
                "totalLikes", totalLikes, "categoryCount", categoryCount,
                "tagCount", tagCount);
    }

    public Map<String, Object> trend(int days) {
        LocalDate start = LocalDate.now().minusDays(days);
        List<Map<String, Object>> articles = jdbcTemplate.queryForList(
                "SELECT DATE(create_time) as date, COUNT(*) as count FROM blog_article " +
                        "WHERE create_time >= ? GROUP BY DATE(create_time) ORDER BY date", start);
        List<Map<String, Object>> views = jdbcTemplate.queryForList(
                "SELECT DATE(create_time) as date, SUM(view_count) as count FROM blog_article " +
                        "WHERE create_time >= ? GROUP BY DATE(create_time) ORDER BY date", start);
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT DATE(create_time) as date, COUNT(*) as count FROM t_user " +
                        "WHERE create_time >= ? GROUP BY DATE(create_time) ORDER BY date", start);
        return Map.of("articles", articles, "views", views, "users", users);
    }

    public List<Map<String, Object>> hotArticles(int limit) {
        return jdbcTemplate.queryForList(
                "SELECT id, title, view_count, like_count, comment_count, status " +
                        "FROM blog_article ORDER BY view_count DESC LIMIT ?", limit);
    }

    public List<Map<String, Object>> categoryStats() {
        return jdbcTemplate.queryForList(
                "SELECT c.id, c.name, COUNT(a.id) as article_count " +
                        "FROM t_category c LEFT JOIN blog_article a ON c.id = a.category_id " +
                        "GROUP BY c.id, c.name ORDER BY article_count DESC");
    }

    // ===================== 定时预聚合 =====================

    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyAggregate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("开始每日数据聚合: date={}", yesterday);

        // 按天聚合文章发布数
        Long articleCount = queryLong(
                "SELECT COUNT(*) FROM blog_article WHERE DATE(create_time) = ?", yesterday);
        saveSummary(yesterday, "article_publish", articleCount, null);

        // 按天聚合用户注册数
        Long userCount = queryLong(
                "SELECT COUNT(*) FROM t_user WHERE DATE(create_time) = ?", yesterday);
        saveSummary(yesterday, "user_register", userCount, null);

        // 按天聚合浏览量
        Long viewCount = queryLong(
                "SELECT COALESCE(SUM(view_count),0) FROM blog_article WHERE DATE(create_time) = ?", yesterday);
        saveSummary(yesterday, "total_views", viewCount, null);

        // 按分类聚合
        List<Map<String, Object>> catStats = jdbcTemplate.queryForList(
                "SELECT category_id, COUNT(*) as cnt FROM blog_article " +
                        "WHERE DATE(create_time) = ? AND category_id IS NOT NULL GROUP BY category_id", yesterday);
        for (Map<String, Object> row : catStats) {
            saveSummary(yesterday, "category_article",
                    ((Number) row.get("cnt")).longValue(),
                    String.valueOf(row.get("category_id")));
        }

        log.info("每日数据聚合完成: date={}", yesterday);
    }

    public List<AnalyticsSummaryPO> getSummary(LocalDate start, LocalDate end, String metric) {
        return summaryMapper.selectList(
                new LambdaQueryWrapper<AnalyticsSummaryPO>()
                        .eq(metric != null, AnalyticsSummaryPO::getMetric, metric)
                        .ge(AnalyticsSummaryPO::getStatDate, start)
                        .le(AnalyticsSummaryPO::getStatDate, end)
                        .orderByAsc(AnalyticsSummaryPO::getStatDate));
    }

    // ===================== 内部方法 =====================

    private void saveSummary(LocalDate date, String metric, Long value, String dimension) {
        AnalyticsSummaryPO po = new AnalyticsSummaryPO();
        po.setStatDate(date);
        po.setMetric(metric);
        po.setValue(value);
        po.setDimension(dimension);
        po.setCreateTime(java.time.LocalDateTime.now());
        summaryMapper.insert(po);
    }

    private Long queryLong(String sql, Object... args) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class, args);
        return result != null ? result : 0L;
    }
}
