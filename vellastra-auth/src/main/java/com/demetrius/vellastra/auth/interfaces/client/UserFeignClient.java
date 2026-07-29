package com.demetrius.vellastra.auth.interfaces.client;

import com.demetrius.vellastra.auth.interfaces.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>Title: UserFeignClient</p>
 * <p>Description: 用户服务 Feign 客户端，用于 auth 模块远程调用 user 模块</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@FeignClient(name = "vellastra-user", path = "/internal/user")
public interface UserFeignClient {

    /**
     * 根据用户ID获取用户基本信息
     *
     * @param id 用户ID
     * @return 用户基本信息
     */
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}