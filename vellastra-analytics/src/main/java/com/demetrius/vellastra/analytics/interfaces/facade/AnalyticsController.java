package com.demetrius.vellastra.analytics.interfaces.facade;

import com.demetrius.vellastra.analytics.application.AnalyticsService;
import com.demetrius.vellastra.analytics.infrastructure.po.AnalyticsSummaryPO;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(analyticsService.overview());
    }

    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestParam(defaultValue = "7") int days) {
        return Result.success(analyticsService.trend(days));
    }

    @GetMapping("/hot-articles")
    public Result<List<Map<String, Object>>> hotArticles(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(analyticsService.hotArticles(limit));
    }

    @GetMapping("/category-stats")
    public Result<List<Map<String, Object>>> categoryStats() {
        return Result.success(analyticsService.categoryStats());
    }

    @GetMapping("/summary")
    public Result<List<AnalyticsSummaryPO>> getSummary(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(7)}") LocalDate start,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate end,
            @RequestParam(required = false) String metric) {
        return Result.success(analyticsService.getSummary(start, end, metric));
    }
}
