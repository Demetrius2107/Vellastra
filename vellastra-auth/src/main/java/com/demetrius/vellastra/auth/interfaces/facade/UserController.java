package com.demetrius.vellastra.auth.interfaces.facade;

import com.demetrius.vellastra.auth.application.UserRoleService;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>Title: UserController</p>
 * <p>Description: 用户管理控制器，提供用户-角色分配等接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRoleService userRoleService;

    public UserController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    // ====================== 用户-角色分配 ======================

    /**
     * 获取用户的所有角色ID
     */
    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        return Result.success(userRoleService.getUserRoleIds(userId));
    }

    /**
     * 分配用户角色（全量覆盖）
     */
    @PutMapping("/{userId}/roles")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userRoleService.assignRoles(userId, roleIds);
        return Result.success();
    }
}