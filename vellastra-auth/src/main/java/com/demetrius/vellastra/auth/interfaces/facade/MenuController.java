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
 * <h3>菜单管理控制器</h3>
 *
 * <p>提供菜单的 CRUD 和树形结构接口。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuApplicationService menuApplicationService;

    public MenuController(MenuApplicationService menuApplicationService) {
        this.menuApplicationService = menuApplicationService;
    }

    @GetMapping("/tree")
    public Result<List<MenuVO>> getMenuTree() {
        return Result.success(menuApplicationService.getMenuTree());
    }

    @GetMapping("/{id}")
    public Result<MenuVO> getMenu(@PathVariable Long id) {
        return Result.success(menuApplicationService.getMenuById(id));
    }

    @PostMapping
    public Result<Long> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        return Result.success(menuApplicationService.createMenu(request));
    }

    @PutMapping("/{id}")
    public Result<Void> updateMenu(@PathVariable Long id, @Valid @RequestBody UpdateMenuRequest request) {
        menuApplicationService.updateMenu(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuApplicationService.deleteMenu(id);
        return Result.success();
    }
}