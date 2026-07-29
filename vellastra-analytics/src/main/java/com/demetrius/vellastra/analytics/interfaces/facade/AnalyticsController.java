package com.demetrius.vellastra.analytics.interfaces.facade;

import com.demetrius.vellastra.analytics.application.AnalyticsService;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    public AnalyticsController(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() { return Result.success(analyticsService.overview()); }

    @GetMapping("/hot-articles")
    public Result<List<Map<String, Object>>> hotArticles(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(analyticsService.hotArticles(limit));
    }
}
