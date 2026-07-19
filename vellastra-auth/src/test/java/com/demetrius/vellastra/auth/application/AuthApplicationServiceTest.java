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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private UserRoleService userRoleService;

    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        authApplicationService = new AuthApplicationService(userRepository, userDomainService, userRoleService);
    }

    @Test
    @DisplayName("login 成功时返回 TokenVO")
    void login_shouldReturnToken() {
        User user = User.builder().id(1L).username("test").password("encoded").status(UserStatus.ENABLED).build();
        List<Long> roleIds = List.of(2L);
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(userDomainService.checkPassword(user, "123456")).thenReturn(true);
        when(userRoleService.getUserRoleIds(1L)).thenReturn(roleIds);
        when(userDomainService.generateToken(user, roleIds)).thenReturn("jwt-token");
        when(userDomainService.getExpireSeconds()).thenReturn(7200L);

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

    @Test
    @DisplayName("login 时查询用户角色列表并传递给 generateToken")
    void login_shouldQueryRolesAndPassToGenerateToken() {
        User user = User.builder().id(1L).username("test").password("encoded").status(UserStatus.ENABLED).build();
        List<Long> roleIds = List.of(2L, 3L);
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(userDomainService.checkPassword(user, "123456")).thenReturn(true);
        when(userRoleService.getUserRoleIds(1L)).thenReturn(roleIds);
        when(userDomainService.generateToken(user, roleIds)).thenReturn("jwt-with-roles");
        when(userDomainService.getExpireSeconds()).thenReturn(7200L);

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("123456");

        TokenVO tokenVO = authApplicationService.login(request);
        assertEquals("jwt-with-roles", tokenVO.getToken());
        verify(userRoleService).getUserRoleIds(1L);
    }

    @Test
    @DisplayName("refresh 时重新查询角色列表并刷新 JWT")
    void refresh_shouldRequeryRoles() {
        User user = User.builder().id(1L).username("test").build();
        List<Long> roleIds = List.of(2L);
        when(userDomainService.validateToken("valid-token")).thenReturn(true);
        when(userDomainService.parseUserId("valid-token")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(user);
        when(userRoleService.getUserRoleIds(1L)).thenReturn(roleIds);
        when(userDomainService.generateToken(user, roleIds)).thenReturn("refreshed-token");
        when(userDomainService.getExpireSeconds()).thenReturn(7200L);

        TokenVO tokenVO = authApplicationService.refresh("valid-token");
        assertEquals("refreshed-token", tokenVO.getToken());
        assertEquals(7200L, tokenVO.getExpireIn());
        verify(userRoleService).getUserRoleIds(1L);
    }

    @Test
    @DisplayName("login 用户被禁用时抛出异常")
    void login_disabledUser_shouldThrow() {
        User user = User.builder().id(1L).username("disabled").password("encoded").status(UserStatus.DISABLED).build();
        when(userRepository.findByUsername("disabled")).thenReturn(Optional.of(user));
        when(userDomainService.checkPassword(user, "123456")).thenReturn(true);

        LoginRequest request = new LoginRequest();
        request.setUsername("disabled");
        request.setPassword("123456");

        assertThrows(BizException.class, () -> authApplicationService.login(request));
    }
}