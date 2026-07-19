package com.demetrius.vellastra.user.interfaces.facade;

import com.demetrius.vellastra.common.response.PageResult;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.user.application.UserApplicationService;
import com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO;
import com.demetrius.vellastra.user.interfaces.dto.in.UserUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: UserController</p>
 * <p>Description: 用户管理控制器，提供用户CRUD、分页查询等接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 * <p>
 * Copyright © 2026 wanqiu All rights reserved
 * @since 1.1
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * 分页查询用户列表
     *
     * @param current 当前页码，默认1
     * @param size    每页大小，默认10
     * @param keyword 搜索关键词，模糊匹配用户名/昵称/邮箱（可选）
     * @param status  状态筛选，0禁用 1正常（可选）
     * @return 分页用户列表
     */
    @GetMapping("/list")
    public Result<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(userApplicationService.listUsers(current, size, keyword, status));
    }

    /**
     * 根据 ID 获取用户详情
     *
     * @param id 用户ID
     * @return 用户视图对象
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        return Result.success(userApplicationService.getUserById(id));
    }

    /**
     * 新增用户
     *
     * @param dto 创建用户请求（用户名、密码、邮箱、昵称等）
     * @return 新用户ID
     */
    @PostMapping
    public Result<Long> createUser(@RequestBody UserCreateDTO dto) {
        return Result.success(userApplicationService.createUser(dto));
    }

    /**
     * 更新用户信息
     *
     * @param id  用户ID
     * @param dto 更新用户请求（昵称、邮箱、头像）
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        userApplicationService.updateUser(id, dto);
        return Result.success();
    }

    /**
     * 逻辑删除用户
     *
     * @param id 用户ID
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userApplicationService.deleteUser(id);
        return Result.success();
    }

    /**
     * 启用/禁用用户
     *
     * @param id     用户ID
     * @param status 1启用 0禁用
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userApplicationService.updateStatus(id, status);
        return Result.success();
    }
}