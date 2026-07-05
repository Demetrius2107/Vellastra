package com.demetrius.blog.auth.application;

import com.demetrius.blog.auth.domain.user.entity.User;
import com.demetrius.blog.auth.domain.user.repository.UserRepository;
import com.demetrius.blog.auth.domain.user.service.UserDomainService;
import com.demetrius.blog.auth.interfaces.dto.LoginRequest;
import com.demetrius.blog.auth.interfaces.dto.RefreshRequest;
import com.demetrius.blog.auth.interfaces.dto.RegisterRequest;
import com.demetrius.blog.auth.interfaces.dto.TokenVO;
import com.demetrius.blog.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

/**
 * <p>Title: AuthApplicationService</p>
 * <p>Description: 鉴权应用服务，负责登录/注册/登出/token刷新等业务逻辑</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;

    public AuthApplicationService(UserRepository userRepository, UserDomainService userDomainService) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
    }

    /**
     * 用户登录
     *
     * @param request 登录请求（用户名 + 密码）
     * @return token 视图对象
     */
    public TokenVO login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(ErrorCode.USER_NOT_FOUND::toException);
        if (!userDomainService.checkPassword(user, request.getPassword())) {
            throw ErrorCode.USER_PASSWORD_ERROR.toException();
        }
        if (!user.isEnabled()) {
            throw ErrorCode.USER_DISABLED.toException();
        }
        return new TokenVO(userDomainService.generateToken(user), 7200L);
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

        User user = userDomainService.createUser(request.getUsername(), request.getPassword(), request.getEmail());
        userRepository.save(user);
    }

    /**
     * 用户登出
     *
     * <p>v1: 无需后端处理，前端删除 token 即可</p>
     *
     * @param token 当前登录 token
     */
    public void logout(String token) {
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
        return new TokenVO(userDomainService.generateToken(user), 7200L);
    }
}
