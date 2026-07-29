package com.demetrius.vellastra.publish.interfaces.facade;

import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.publish.application.PublishEngineService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publish")
public class PublishController {

    private final PublishEngineService publishEngineService;

    public PublishController(PublishEngineService publishEngineService) {
        this.publishEngineService = publishEngineService;
    }

    @PostMapping("/trigger")
    public Result<Long> triggerPublish(@RequestParam Long articleId, @RequestParam(defaultValue = "deploy") String action) {
        return Result.success(publishEngineService.triggerPublish(articleId, action));
    }
}
