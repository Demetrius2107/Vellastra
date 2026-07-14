package com.demetrius.vellastra.user.interfaces.facade;

import com.demetrius.vellastra.user.application.UserApplicationService;
import com.demetrius.vellastra.user.interfaces.dto.in.UserUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: 用户控制器</p>
 * <p>Description: 用户模块RESTful接口控制器，提供用户查询、更新等API</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 * @since 1.1
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        return Result.success(userApplicationService.getUserById(id));
    }

    @GetMapping("/info")
    public Result<UserVO> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(userApplicationService.getUserById(userId));
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        userApplicationService.updateUser(id, userUpdateDTO);
        return Result.success();
    }

}
