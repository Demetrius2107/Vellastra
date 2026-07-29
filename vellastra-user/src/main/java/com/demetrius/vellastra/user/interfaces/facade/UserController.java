package com.demetrius.vellastra.user.interfaces.facade;

import com.demetrius.vellastra.common.response.PageResult;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.user.application.UserApplicationService;
import com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO;
import com.demetrius.vellastra.user.interfaces.dto.in.UserInfoUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.in.PasswordUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.in.UserUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import com.demetrius.vellastra.common.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: UserController</p>
 * <p>Description: 用户管理控制器，提供用户CRUD、分页查询、密码管理等接口</p>
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
     * 获取当前登录用户信息
     *
     * <p>从请求头 X-User-Id 获取当前用户ID，返回完整个人信息。</p>
     *
     * @param httpRequest HTTP 请求
     * @return 当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getCurrentUserInfo(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromHeader(httpRequest);
        return Result.success(userApplicationService.getCurrentUserInfo(userId));
    }

    /**
     * 更新当前登录用户信息
     *
     * <p>修改当前用户的昵称、头像、个人简介。</p>
     *
     * @param dto         用户信息更新请求
     * @param httpRequest HTTP 请求
     */
    @PutMapping("/info")
    public Result<Void> updateCurrentUserInfo(@RequestBody UserInfoUpdateDTO dto,
                                              HttpServletRequest httpRequest) {
        Long userId = getUserIdFromHeader(httpRequest);
        userApplicationService.updateCurrentUserInfo(userId, dto);
        return Result.success();
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

    /**
     * 管理员重置用户密码
     *
     * <p>将用户密码重置为 "123456"。重置后应告知用户尽快修改密码。</p>
     *
     * @param id 用户ID
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userApplicationService.resetPassword(id);
        return Result.success();
    }

    /**
     * 用户自助修改密码
     *
     * <p>校验旧密码正确后，更新为新密码。新密码长度至少6位。</p>
     *
     * @param dto          修改密码请求（旧密码 + 新密码）
     * @param httpRequest  HTTP 请求（从 X-User-Id 请求头获取用户ID）
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody PasswordUpdateDTO dto,
                                       HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest, dto);
        userApplicationService.changePassword(userId, dto);
        return Result.success();
    }

    /**
     * 从请求头或 DTO 中获取用户ID
     *
     * @param request HTTP 请求
     * @param dto     请求体 DTO
     * @return 用户ID，无法获取时抛出异常
     */
    private Long getUserId(HttpServletRequest request, PasswordUpdateDTO dto) {
        Long userId = getUserIdFromHeader(request);
        if (userId != null) return userId;
        // 降级：从 DTO 中获取
        if (dto.getUserId() == null) {
            throw new BizException(400, "无法获取用户ID，请登录后重试");
        }
        return dto.getUserId();
    }

    /**
     * 从请求头 X-User-Id 获取用户ID
     *
     * @param request HTTP 请求
     * @return 用户ID，请求头不存在或格式错误时返回 null
     */
    private Long getUserIdFromHeader(HttpServletRequest request) {
        String headerUserId = request.getHeader("X-User-Id");
        if (headerUserId != null && !headerUserId.isEmpty()) {
            try {
                return Long.parseLong(headerUserId);
            } catch (NumberFormatException e) {
                throw new BizException(400, "请求头 X-User-Id 格式错误");
            }
        }
        return null;
    }
}