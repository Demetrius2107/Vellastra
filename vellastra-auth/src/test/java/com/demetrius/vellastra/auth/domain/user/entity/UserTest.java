package com.demetrius.vellastra.auth.domain.user.service;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Title: UserTest</p>
 * <p>Description: 用户领域实体单元测试</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
class UserTest {

    @Test
    @DisplayName("isEnabled() 状态为 ENABLED 时返回 true")
    void isEnabled_whenEnabled_shouldReturnTrue() {
        User user = User.builder().status(UserStatus.ENABLED).build();
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("isEnabled() 状态为 DISABLED 时返回 false")
    void isEnabled_whenDisabled_shouldReturnFalse() {
        User user = User.builder().status(UserStatus.DISABLED).build();
        assertFalse(user.isEnabled());
    }

    @Test
    @DisplayName("disable() 应将状态设为 DISABLED")
    void disable_shouldSetStatusToDisabled() {
        User user = User.builder().status(UserStatus.ENABLED).build();
        user.disable();
        assertEquals(UserStatus.DISABLED, user.getStatus());
    }

    @Test
    @DisplayName("enable() 应将状态设为 ENABLED")
    void enable_shouldSetStatusToEnabled() {
        User user = User.builder().status(UserStatus.DISABLED).build();
        user.enable();
        assertEquals(UserStatus.ENABLED, user.getStatus());
    }
}