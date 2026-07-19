package com.demetrius.vellastra.auth.interfaces.facade;

import com.demetrius.vellastra.auth.application.UserRoleService;
import com.demetrius.vellastra.common.response.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <p>Title: UserControllerTest</p>
 * <p>Description: 用户管理控制器单元测试</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRoleService userRoleService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userRoleService);
    }

    @Test
    @DisplayName("getUserRoleIds 应返回用户角色ID列表")
    void getUserRoleIds_shouldReturnRoleIds() {
        when(userRoleService.getUserRoleIds(1L)).thenReturn(List.of(1L, 2L));

        Result<List<Long>> result = userController.getUserRoleIds(1L);

        assertEquals(200, result.getCode());
        assertEquals(2, result.getData().size());
        assertTrue(result.getData().contains(1L));
        assertTrue(result.getData().contains(2L));
        verify(userRoleService).getUserRoleIds(1L);
    }

    @Test
    @DisplayName("getUserRoleIds 用户无角色时返回空列表")
    void getUserRoleIds_noRoles_shouldReturnEmpty() {
        when(userRoleService.getUserRoleIds(99L)).thenReturn(List.of());

        Result<List<Long>> result = userController.getUserRoleIds(99L);

        assertEquals(200, result.getCode());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("assignRoles 应调用服务分配角色")
    void assignRoles_shouldCallService() {
        List<Long> roleIds = List.of(1L, 2L);

        Result<Void> result = userController.assignRoles(1L, roleIds);

        assertEquals(200, result.getCode());
        verify(userRoleService).assignRoles(1L, roleIds);
    }

    @Test
    @DisplayName("assignRoles 空角色列表应清空用户角色")
    void assignRoles_emptyList_shouldClearRoles() {
        List<Long> emptyList = List.of();

        Result<Void> result = userController.assignRoles(1L, emptyList);

        assertEquals(200, result.getCode());
        verify(userRoleService).assignRoles(1L, emptyList);
    }
}