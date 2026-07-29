package com.demetrius.vellastra.publish.interfaces.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.publish.application.PublishEngineService;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishBuildLogPO;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishTaskPO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publish")
public class PublishController {

    private final PublishEngineService publishEngineService;

    public PublishController(PublishEngineService publishEngineService) {
        this.publishEngineService = publishEngineService;
    }

    @PostMapping("/trigger")
    public Result<Long> triggerPublish(@RequestParam Long articleId,
                                       @RequestParam(defaultValue = "") String articleTitle,
                                       @RequestParam(defaultValue = "deploy") String action,
                                       @RequestHeader("X-User-Id") Long userId) {
        Long taskId = publishEngineService.createTask(articleId, articleTitle, action, String.valueOf(userId));
        publishEngineService.executeBuild(taskId);
        return Result.success(taskId);
    }

    @GetMapping("/tasks")
    public Result<IPage<PublishTaskPO>> listTasks(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.success(publishEngineService.listTasks(current, size, status));
    }

    @GetMapping("/tasks/{id}/logs")
    public Result<List<PublishBuildLogPO>> getLogs(@PathVariable Long id) {
        return Result.success(publishEngineService.getLogs(id));
    }

    @PostMapping("/tasks/{id}/retry")
    public Result<Void> retry(@PathVariable Long id) {
        publishEngineService.retry(id);
        return Result.success();
    }
}
