package com.demetrius.vellastra.user.interfaces.facade;

import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.user.application.UserApplicationService;
import com.demetrius.vellastra.user.interfaces.dto.in.UserInternalDTO;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: UserInternalController</p>
 * <p>Description: 用户服务内部 API 控制器，供其他模块 Feign 调用</p>
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
@RequestMapping("/internal/user")
public class UserInternalController {

    private final UserApplicationService userApplicationService;

    public UserInternalController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * 根据用户ID获取用户基本信息（Feign 内部调用）
     *
     * @param id 用户ID
     * @return 用户基本信息
     */
    @GetMapping("/{id}")
    public Result<UserInternalDTO> getUserById(@PathVariable Long id) {
        return Result.success(userApplicationService.getUserInternalById(id));
    }
}