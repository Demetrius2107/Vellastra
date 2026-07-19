package com.demetrius.vellastra.auth.application;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.repository.UserRepository;
import com.demetrius.vellastra.auth.domain.user.service.UserDomainService;
import com.demetrius.vellastra.auth.interfaces.dto.LoginRequest;
import com.demetrius.vellastra.auth.interfaces.dto.RefreshRequest;
import com.demetrius.vellastra.auth.interfaces.dto.RegisterRequest;
import com.demetrius.vellastra.auth.interfaces.dto.TokenVO;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.common.service.LoginAttemptService;
import com.demetrius.vellastra.common.service.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>Title: AuthApplicationService</p>
 * <p>Description: 鉴权应用服务，负责登录/注册/登出/token刷新等业务逻辑</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final UserRoleService userRoleService;
    private final TokenBlackListService tokenBlackListService;
    private final LoginAttemptService loginAttemptService;
    private final LoginLogService loginLogService;

    public AuthApplicationService(UserRepository userRepository, UserDomainService userDomainService,
                                  UserRoleService userRoleService, TokenBlackListService tokenBlackListService,
                                  LoginAttemptService loginAttemptService, LoginLogService loginLogService) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.userRoleService = userRoleService;
        this.tokenBlackListService = tokenBlackListService;
        this.loginAttemptService = loginAttemptService;
        this.loginLogService = loginLogService;
    }

    /**
     * 用户登录
     *
     * @param request 登录请求（用户名 + 密码）
     * @param httpRequest HTTP 请求对象（用于记录登录日志的 IP 和浏览器信息）
     * @return token 视图对象
     */
    public TokenVO login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername();

        // 1. 检查账号是否被锁定
        if (loginAttemptService.isLocked(username)) {
            long remaining = loginAttemptService.getRemainingLockSeconds(username);
            String msg = String.format("账号已被锁定，请%d分钟后再试", remaining / 60 + 1);
            loginLogService.record(null, username, false, msg, httpRequest);
            throw new BizException(ErrorCode.USER_LOCKED.getCode(), msg);
        }

        // 2. 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(ErrorCode.USER_NOT_FOUND::toException);

        // 3. 校验密码
        if (!userDomainService.checkPassword(user, request.getPassword())) {
            loginAttemptService.loginFailed(username);
            loginLogService.record(user.getId(), username, false, "密码错误", httpRequest);
            throw ErrorCode.USER_PASSWORD_ERROR.toException();
        }

        // 4. 检查用户状态
        if (!user.isEnabled()) {
            loginLogService.record(user.getId(), username, false, "用户已被禁用", httpRequest);
            throw ErrorCode.USER_DISABLED.toException();
        }

        // 5. 登录成功，清除失败记录
        loginAttemptService.loginSucceeded(username);

        // 6. 生成 Token
        List<Long> roleIds = userRoleService.getUserRoleIds(user.getId());
        TokenVO tokenVO = new TokenVO(userDomainService.generateToken(user, roleIds), userDomainService.getExpireSeconds());

        // 7. 记录登录成功日志
        loginLogService.record(user.getId(), username, true, "登录成功", httpRequest);

        return tokenVO;
    }

    /**
     * 用户注册
     *
     * @param request 注册请求（用户名 + 密码 + 邮箱）
     */
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw ErrorCode.USER_ALREADY_EXISTS.toException();
        }

        // 校验密码强度
        userDomainService.validatePasswordStrength(request.getPassword());

        User user = userDomainService.createUser(request.getUsername(), request.getPassword(), request.getEmail());
        userRepository.save(user);
    }

    /**
     * 用户登出
     *
     * <p>将 Token 加入黑名单，使其失效。</p>
     *
     * @param token 当前登录 token
     */
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null && !token.isEmpty()) {
            tokenBlackListService.add(token, userDomainService.getExpireAtMillis(token));
        }
    }

    /**
     * 刷新 token
     *
     * @param token 旧 token（尚未过期）
     * @return 新 token 视图对象
     */
    public TokenVO refresh(String token) {
        if (!userDomainService.validateToken(token)) {
            throw ErrorCode.TOKEN_INVALID.toException();
        }
        Long userId = userDomainService.parseUserId(token);
        User user = userRepository.findById(userId);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        List<Long> roleIds = userRoleService.getUserRoleIds(user.getId());
        return new TokenVO(userDomainService.generateToken(user, roleIds), userDomainService.getExpireSeconds());
    }
}
