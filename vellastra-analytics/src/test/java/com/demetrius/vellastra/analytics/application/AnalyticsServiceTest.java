package com.demetrius.vellastra.analytics.application;

import com.demetrius.vellastra.analytics.infrastructure.mapper.AnalyticsDailyMapper;
import com.demetrius.vellastra.analytics.infrastructure.po.AnalyticsDailyPO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * AnalyticsService 使用 JdbcTemplate 多重重载，精确 stub 复杂，
 * 使用 LENIENT 严格级别避免重载匹配导致的 PotentialStubbingProblem。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AnalyticsServiceTest {

    @Mock
    private JdbcTemplate jdbc;

    @Mock
    private AnalyticsDailyMapper dailyMapper;

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsService(jdbc, dailyMapper);
    }

    @Test
    @DisplayName("dashboard 应返回全部统计指标")
    void dashboard_shouldReturnMetrics() {
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(10L);
        // queryForObject(String, Long.class) 无参版本
        when(jdbc.queryForObject(contains("SELECT COUNT(*)"), eq(Long.class))).thenReturn(10L);
        when(jdbc.queryForObject(contains("SELECT COALESCE(SUM"), eq(Long.class))).thenReturn(100L);

        Map<String, Object> result = analyticsService.dashboard();

        assertNotNull(result);
        assertTrue(result.containsKey("totalArticles"));
        assertTrue(result.containsKey("totalViews"));
    }

    @Test
    @DisplayName("trend 应返回指定指标的趋势数据")
    void trend_shouldReturnTrend() {
        when(dailyMapper.trend(eq("article_publish"), any(), any())).thenReturn(
                List.of(Map.of("stat_date", "2026-08-01", "value", 5L)));

        Map<String, Object> result = analyticsService.trend("article_publish", 30);

        assertEquals("article_publish", result.get("metric"));
        assertNotNull(result.get("data"));
    }

    @Test
    @DisplayName("dailyAggregate 应保存预聚合数据")
    void dailyAggregate_shouldSave() {
        when(jdbc.queryForObject(contains("SELECT COUNT(*)"), eq(Long.class))).thenReturn(3L);
        when(jdbc.queryForObject(contains("SELECT COALESCE"), eq(Long.class))).thenReturn(15L);

        analyticsService.dailyAggregate();

        verify(dailyMapper, atLeast(4)).insert(any(AnalyticsDailyPO.class));
    }
}
