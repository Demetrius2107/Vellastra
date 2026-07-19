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

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/list")
    public Result<List<RoleVO>> listRoles() {
        return Result.success(roleApplicationService.listRoles());
    }

    /**
     * 根据 ID 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    public Result<RoleVO> getRole(@PathVariable Long id) {
        return Result.success(roleApplicationService.getRoleById(id));
    }

    /**
     * 创建角色
     *
     * @param request 创建角色请求
     * @return 新角色 ID
     */
    @PostMapping
    public Result<Long> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return Result.success(roleApplicationService.createRole(request));
    }

    /**
     * 更新角色信息
     *
     * @param id      角色ID
     * @param request 更新角色请求
     */
    @PutMapping("/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        roleApplicationService.updateRole(id, request);
        return Result.success();
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleApplicationService.deleteRole(id);
        return Result.success();
    }

    // ====================== 角色-菜单权限分配 ======================

    /**
     * 分配角色菜单权限（全量覆盖）
     *
     * @param id      角色ID
     * @param menuIds 菜单ID列表
     */
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleMenuService.assignMenus(id, menuIds);
        return Result.success();
    }

    /**
     * 获取角色已分配的菜单ID列表
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/{id}/menu-ids")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return Result.success(roleMenuService.getRoleMenuIds(id));
    }
}