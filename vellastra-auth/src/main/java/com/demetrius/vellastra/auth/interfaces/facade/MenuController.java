package com.demetrius.vellastra.auth.interfaces.facade;

import com.demetrius.vellastra.auth.application.MenuApplicationService;
import com.demetrius.vellastra.auth.interfaces.dto.CreateMenuRequest;
import com.demetrius.vellastra.auth.interfaces.dto.MenuVO;
import com.demetrius.vellastra.auth.interfaces.dto.UpdateMenuRequest;
import com.demetrius.vellastra.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>Title: MenuController</p>
 * <p>Description: 菜单管理控制器，提供菜单的 CRUD 和树形结构接口</p>
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
@RequestMapping("/menu")
public class MenuController {

    private final MenuApplicationService menuApplicationService;

    public MenuController(MenuApplicationService menuApplicationService) {
        this.menuApplicationService = menuApplicationService;
    }

    /**
     * 获取菜单树
     *
     * @return 树形菜单列表
     */
    @GetMapping("/tree")
    public Result<List<MenuVO>> getMenuTree() {
        return Result.success(menuApplicationService.getMenuTree());
    }

    /**
     * 根据 ID 获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{id}")
    public Result<MenuVO> getMenu(@PathVariable Long id) {
        return Result.success(menuApplicationService.getMenuById(id));
    }

    /**
     * 创建菜单
     *
     * @param request 创建菜单请求
     * @return 新菜单 ID
     */
    @PostMapping
    public Result<Long> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        return Result.success(menuApplicationService.createMenu(request));
    }

    /**
     * 更新菜单信息
     *
     * @param id      菜单ID
     * @param request 更新菜单请求
     */
    @PutMapping("/{id}")
    public Result<Void> updateMenu(@PathVariable Long id, @Valid @RequestBody UpdateMenuRequest request) {
        menuApplicationService.updateMenu(id, request);
        return Result.success();
    }

    /**
     * 删除菜单（有子菜单时不允许删除）
     *
     * @param id 菜单ID
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuApplicationService.deleteMenu(id);
        return Result.success();
    }
}