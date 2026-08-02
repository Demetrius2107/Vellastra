package com.demetrius.vellastra.mail.interfaces.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.mail.application.SubscriberService;
import com.demetrius.vellastra.mail.infrastructure.po.SubscriberPO;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: SubscriberController</p>
 * <p>Description: 订阅者管理控制器，提供订阅、确认、退订、列表等接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("/mail/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping
    public Result<Long> subscribe(@RequestParam String email, @RequestParam(required = false) String name) {
        return Result.success(subscriberService.subscribe(email, name));
    }

    @GetMapping("/confirm")
    public Result<Boolean> confirm(@RequestParam String token) {
        return Result.success(subscriberService.confirm(token));
    }

    @GetMapping("/unsubscribe")
    public Result<Boolean> unsubscribe(@RequestParam String token) {
        return Result.success(subscriberService.unsubscribe(token));
    }

    @GetMapping
    public Result<IPage<SubscriberPO>> list(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String status) {
        return Result.success(subscriberService.list(current, size, status));
    }

    @GetMapping("/count")
    public Result<Long> countConfirmed() {
        return Result.success(subscriberService.countConfirmed());
    }
}
