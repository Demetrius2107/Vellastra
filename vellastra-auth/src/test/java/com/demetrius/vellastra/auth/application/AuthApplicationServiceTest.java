package com.demetrius.vellastra.auth.application;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.repository.UserRepository;
import com.demetrius.vellastra.auth.domain.user.service.UserDomainService;
import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.auth.interfaces.dto.LoginRequest;
import com.demetrius.vellastra.auth.interfaces.dto.RegisterRequest;
import com.demetrius.vellastra.auth.interfaces.dto.TokenVO;
import com.demetrius.vellastra.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Title: AuthApplicationServiceTest</p>
 * <p>Description: 鉴权应用服务单元测试</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainService userDomainService;

    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        authApplicationService = new AuthApplicationService(userRepository, userDomainService);
    }

    @Test
    @DisplayName("login 成功时返回 TokenVO")
    void login_shouldReturnToken() {
        User user = User.builder().id(1L).username("test").password("encoded").status(UserStatus.ENABLED).build();
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(userDomainService.checkPassword(user, "123456")).thenReturn(true);
        when(userDomainService.generateToken(user)).thenReturn("jwt-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("123456");

        TokenVO tokenVO = authApplicationService.login(request);
        assertEquals("jwt-token", tokenVO.getToken());
        assertEquals(7200L, tokenVO.getExpireIn());
    }

    @Test
    @DisplayName("login 用户名不存在时抛出异常")
    void login_userNotFound_shouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("123456");

        assertThrows(BizException.class, () -> authApplicationService.login(request));
    }

    @Test
    @DisplayName("login 密码错误时抛出异常")
    void login_wrongPassword_shouldThrow() {
        User user = User.builder().id(1L).username("test").password("encoded").status(UserStatus.ENABLED).build();
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(userDomainService.checkPassword(user, "wrong")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("wrong");

        assertThrows(BizException.class, () -> authApplicationService.login(request));
    }

    @Test
    @DisplayName("register 用户名重复时抛出异常")
    void register_duplicateUsername_shouldThrow() {
        when(userRepository.existsByUsername("test")).thenReturn(true);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("test");
        request.setPassword("123456");
        request.setEmail("test@test.com");

        assertThrows(BizException.class, () -> authApplicationService.register(request));
    }

    @Test
    @DisplayName("register 成功时保存用户")
    void register_shouldSaveUser() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        User newUser = User.builder().id(1L).username("newuser").build();
        when(userDomainService.createUser("newuser", "123456", "new@test.com")).thenReturn(newUser);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setEmail("new@test.com");

        authApplicationService.register(request);
        verify(userRepository).save(newUser);
    }
}