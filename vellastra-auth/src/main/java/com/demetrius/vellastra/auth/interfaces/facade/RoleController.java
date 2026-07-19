package com.demetrius.vellastra.auth.interfaces.facade;

import com.demetrius.vellastra.auth.application.RoleApplicationService;
import com.demetrius.vellastra.auth.application.RoleMenuService;
import com.demetrius.vellastra.auth.interfaces.dto.CreateRoleRequest;
import com.demetrius.vellastra.auth.interfaces.dto.RoleVO;
import com.demetrius.vellastra.auth.interfaces.dto.UpdateRoleRequest;
import com.demetrius.vellastra.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>Title: RoleController</p>
 * <p>Description: 角色管理控制器，提供角色的 CRUD 和菜单权限分配接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleApplicationService roleApplicationService;
    private final RoleMenuService roleMenuService;

    public RoleController(RoleApplicationService roleApplicationService,
                          RoleMenuService roleMenuService) {
        this.roleApplicationService = roleApplicationService;
        this.roleMenuService = roleMenuService;
    }

    @GetMapping("/list")
    public Result<List<RoleVO>> listRoles() {
        return Result.success(roleApplicationService.listRoles());
    }

    @GetMapping("/{id}")
    public Result<RoleVO> getRole(@PathVariable Long id) {
        return Result.success(roleApplicationService.getRoleById(id));
    }

    @PostMapping
    public Result<Long> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return Result.success(roleApplicationService.createRole(request));
    }

    @PutMapping("/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        roleApplicationService.updateRole(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleApplicationService.deleteRole(id);
        return Result.success();
    }

    // ====================== 角色-菜单权限分配 ======================

    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleMenuService.assignMenus(id, menuIds);
        return Result.success();
    }

    @GetMapping("/{id}/menu-ids")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return Result.success(roleMenuService.getRoleMenuIds(id));
    }
}