package com.demetrius.vellastra.user.interfaces.facade;

import com.demetrius.vellastra.common.response.PageResult;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.user.application.UserApplicationService;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: UserController</p>
 * <p>Description: 用户管理控制器，提供用户分页查询等接口</p>
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
     * @param current  当前页码（默认1）
     * @param size     每页大小（默认10）
     * @param keyword  搜索关键词（模糊匹配用户名/昵称/邮箱）
     * @param status   状态筛选
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(userApplicationService.listUsers(current, size, keyword, status));
    }
}