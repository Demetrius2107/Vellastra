package com.demetrius.vellastra.auth.interfaces.facade;

import com.demetrius.vellastra.auth.application.AuthApplicationService;
import com.demetrius.vellastra.auth.interfaces.dto.LoginRequest;
import com.demetrius.vellastra.auth.interfaces.dto.RegisterRequest;
import com.demetrius.vellastra.auth.interfaces.dto.TokenVO;
import com.demetrius.vellastra.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: AuthController</p>
 * <p>Description: 鉴权控制器，处理登录/注册/登出/token刷新</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    /**
     * 用户登录
     *
     * @param request     登录请求（用户名 + 密码）
     * @param httpRequest HTTP 请求（用于记录登录日志）
     * @return TokenVO
     */
    @PostMapping("/login")
    public Result<TokenVO> login(@Valid @RequestBody LoginRequest request,
                                 HttpServletRequest httpRequest) {
        return Result.success(authApplicationService.login(request, httpRequest));
    }

    /**
     * 用户注册
     *
     * @param request 注册请求（用户名 + 密码 + 邮箱）
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authApplicationService.register(request);
        return Result.success();
    }

    /**
     * 用户登出（将 Token 加入黑名单）
     *
     * @param token Authorization 请求头中的 JWT
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        authApplicationService.logout(token);
        return Result.success();
    }

    /**
     * 刷新 Token（旧 Token 尚未过期时换取新 Token）
     *
     * @param token Authorization 请求头中的 JWT
     * @return 新 TokenVO
     */
    @PostMapping("/refresh")
    public Result<TokenVO> refresh(@RequestHeader("Authorization") String token) {
        return Result.success(authApplicationService.refresh(token));
    }

}