package com.demetrius.vellastra.analytics.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private final JdbcTemplate jdbcTemplate;
    public AnalyticsService(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public Map<String, Object> overview() {
        Long articleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM blog_article", Long.class);
        Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_user", Long.class);
        Long commentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_comment", Long.class);
        Long totalViews = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(view_count),0) FROM blog_article", Long.class);
        return Map.of("articleCount", articleCount, "userCount", userCount,
                "commentCount", commentCount, "totalViews", totalViews);
    }

    public List<Map<String, Object>> hotArticles(int limit) {
        return jdbcTemplate.queryForList(
                "SELECT id, title, view_count FROM blog_article ORDER BY view_count DESC LIMIT ?", limit);
    }
}
