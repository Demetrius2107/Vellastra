package com.demetrius.vellastra.user.interfaces.facade;

import com.demetrius.vellastra.user.application.UserApplicationService;
import com.demetrius.vellastra.user.interfaces.dto.UserVO;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.web.bind.annotation.*;

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
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserVO userVO) {
        userApplicationService.updateUser(id, userVO);
        return Result.success();
    }

}
