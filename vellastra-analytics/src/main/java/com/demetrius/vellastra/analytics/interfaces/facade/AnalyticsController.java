package com.demetrius.vellastra.analytics.interfaces.facade;

import com.demetrius.vellastra.analytics.application.AnalyticsService;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() { return Result.success(analyticsService.dashboard()); }

    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestParam(defaultValue = "article_publish") String metric,
                                             @RequestParam(defaultValue = "30") int days) {
        return Result.success(analyticsService.trend(metric, days));
    }

    @GetMapping("/trend/all")
    public Result<Map<String, Object>> multiTrend(@RequestParam(defaultValue = "30") int days) {
        return Result.success(analyticsService.multiTrend(days));
    }

    @GetMapping("/hot-articles")
    public Result<List<Map<String, Object>>> hotArticles(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(analyticsService.hotArticles(limit));
    }

    @GetMapping("/category-stats")
    public Result<List<Map<String, Object>>> categoryStats() {
        return Result.success(analyticsService.categoryStats());
    }

    @GetMapping("/author-stats")
    public Result<List<Map<String, Object>>> authorStats(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(analyticsService.authorStats(limit));
    }

    @GetMapping("/export/{type}")
    public ResponseEntity<String> export(@PathVariable String type,
                                          @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") LocalDate start,
                                          @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate end) {
        String csv = analyticsService.exportCsv(type, start, end);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "_" + start + "_" + end + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
