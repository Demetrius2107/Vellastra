package com.demetrius.vellastra.common.config;

import com.demetrius.vellastra.common.service.IdempotentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>Title: IdempotentController</p>
 * <p>Description: 幂等 Token 生成控制器，客户端请求前先获取 Token</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("/idempotent")
public class IdempotentController {

    private final IdempotentService idempotentService;

    public IdempotentController(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    /**
     * 获取幂等 Token
     *
     * @return {"token": "uuid-string"}
     */
    @GetMapping("/token")
    public Map<String, String> getToken() {
        return Map.of("token", idempotentService.generateToken());
    }
}