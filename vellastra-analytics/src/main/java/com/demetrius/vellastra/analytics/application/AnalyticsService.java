package com.demetrius.vellastra.analytics.application;

import com.demetrius.vellastra.analytics.infrastructure.mapper.AnalyticsDailyMapper;
import com.demetrius.vellastra.analytics.infrastructure.po.AnalyticsDailyPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class AnalyticsService {

    private final JdbcTemplate jdbc;
    private final AnalyticsDailyMapper dailyMapper;

    public AnalyticsService(JdbcTemplate jdbc, AnalyticsDailyMapper dailyMapper) {
        this.jdbc = jdbc;
        this.dailyMapper = dailyMapper;
    }

    // ===================== 实时总览 =====================

    public Map<String, Object> dashboard() {
        Long articles = q("SELECT COUNT(*) FROM blog_article");
        Long users = q("SELECT COUNT(*) FROM t_user");
        Long comments = q("SELECT COUNT(*) FROM t_comment");
        Long views = q("SELECT COALESCE(SUM(view_count),0) FROM blog_article");
        Long likes = q("SELECT COALESCE(SUM(like_count),0) FROM blog_article");
        Long categories = q("SELECT COUNT(*) FROM t_category");
        Long tags = q("SELECT COUNT(*) FROM t_tag");
        Long todayArticles = q("SELECT COUNT(*) FROM blog_article WHERE DATE(create_time) = CURDATE()");
        Long todayViews = q("SELECT COALESCE(SUM(view_count),0) FROM blog_article WHERE DATE(create_time) = CURDATE()");

        return Map.of(
                "totalArticles", articles, "totalUsers", users,
                "totalComments", comments, "totalViews", views,
                "totalLikes", likes, "totalCategories", categories,
                "totalTags", tags, "todayArticles", todayArticles,
                "todayViews", todayViews);
    }

    // ===================== 趋势分析 =====================

    public Map<String, Object> trend(String metric, int days) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);
        List<Map<String, Object>> raw = dailyMapper.trend(metric, start, end);
        return Map.of("metric", metric, "start", start.toString(),
                "end", end.toString(), "data", raw);
    }

    public Map<String, Object> multiTrend(int days) {
        return Map.of(
                "articles", trend("article_publish", days).get("data"),
                "views", trend("total_views", days).get("data"),
                "users", trend("user_register", days).get("data"),
                "comments", trend("comment_create", days).get("data"));
    }

    // ===================== 多维统计 =====================

    public List<Map<String, Object>> categoryStats() {
        return jdbc.queryForList(
                "SELECT c.id, c.name, COUNT(a.id) as article_count, " +
                        "COALESCE(SUM(a.view_count),0) as total_views " +
                        "FROM t_category c LEFT JOIN blog_article a ON c.id = a.category_id " +
                        "GROUP BY c.id, c.name ORDER BY article_count DESC");
    }

    public List<Map<String, Object>> authorStats(int limit) {
        return jdbc.queryForList(
                "SELECT author_id, COUNT(*) as article_count, COALESCE(SUM(view_count),0) as total_views " +
                        "FROM blog_article GROUP BY author_id ORDER BY article_count DESC LIMIT ?", limit);
    }

    public List<Map<String, Object>> hotArticles(int limit) {
        return jdbc.queryForList(
                "SELECT id, title, view_count, like_count, comment_count, status, create_time " +
                        "FROM blog_article ORDER BY view_count DESC LIMIT ?", limit);
    }

    // ===================== 数据导出 =====================

    public String exportCsv(String type, LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case "articles" -> {
                sb.append("ID,标题,状态,浏览量,点赞数,评论数,创建时间\n");
                jdbc.queryForList("SELECT * FROM blog_article WHERE DATE(create_time) BETWEEN ? AND ? ORDER BY create_time", start, end)
                        .forEach(r -> sb.append(r.get("id")).append(",")
                                .append(escapeCsv((String) r.get("title"))).append(",")
                                .append(r.get("status")).append(",")
                                .append(r.get("view_count")).append(",")
                                .append(r.get("like_count")).append(",")
                                .append(r.get("comment_count")).append(",")
                                .append(r.get("create_time")).append("\n"));
            }
            case "users" -> {
                sb.append("ID,用户名,昵称,邮箱,状态,创建时间\n");
                jdbc.queryForList("SELECT id,username,nickname,email,status,create_time FROM t_user WHERE DATE(create_time) BETWEEN ? AND ? ORDER BY create_time", start, end)
                        .forEach(r -> sb.append(r.get("id")).append(",")
                                .append(r.get("username")).append(",")
                                .append(escapeCsv((String) r.get("nickname"))).append(",")
                                .append(r.get("email")).append(",")
                                .append(r.get("status")).append(",")
                                .append(r.get("create_time")).append("\n"));
            }
        }
        return sb.toString();
    }

    // ===================== 定时预聚合 =====================

    @Scheduled(cron = "${analytics.cron:0 30 2 * * ?}")
    @Transactional
    public void dailyAggregate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("开始每日数据聚合: {}", yesterday);
        saveDaily(yesterday, "article_publish",
                q("SELECT COUNT(*) FROM blog_article WHERE DATE(create_time) = ?", yesterday));
        saveDaily(yesterday, "user_register",
                q("SELECT COUNT(*) FROM t_user WHERE DATE(create_time) = ?", yesterday));
        saveDaily(yesterday, "total_views",
                q("SELECT COALESCE(SUM(view_count),0) FROM blog_article WHERE DATE(create_time) = ?", yesterday));
        saveDaily(yesterday, "comment_create",
                q("SELECT COUNT(*) FROM t_comment WHERE DATE(create_time) = ?", yesterday));
        jdbc.queryForList("SELECT category_id, COUNT(*) as cnt FROM blog_article " +
                        "WHERE DATE(create_time) = ? AND category_id IS NOT NULL GROUP BY category_id", yesterday)
                .forEach(r -> saveDaily(yesterday, "category_article",
                        ((Number) r.get("cnt")).longValue(),
                        String.valueOf(r.get("category_id"))));
        log.info("每日数据聚合完成: {}", yesterday);
    }

    // ===================== 内部 =====================

    private void saveDaily(LocalDate date, String metric, Long value) {
        saveDaily(date, metric, value, null);
    }

    private void saveDaily(LocalDate date, String metric, Long value, String dim) {
        AnalyticsDailyPO po = new AnalyticsDailyPO();
        po.setStatDate(date); po.setMetric(metric); po.setValue(value != null ? value : 0L);
        po.setDimension(dim); po.setCreateTime(java.time.LocalDateTime.now());
        dailyMapper.insert(po);
    }

    private Long q(String sql, Object... args) {
        Long r = jdbc.queryForObject(sql, Long.class, args);
        return r != null ? r : 0L;
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}
