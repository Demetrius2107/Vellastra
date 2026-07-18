package com.demetrius.vellastra.auth.domain.user.service;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link User} domain entity
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