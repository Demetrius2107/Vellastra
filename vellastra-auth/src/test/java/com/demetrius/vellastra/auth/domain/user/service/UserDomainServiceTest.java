package com.demetrius.vellastra.auth.domain.user.service;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Title: UserDomainServiceTest</p>
 * <p>Description: 用户领域服务单元测试</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
class UserDomainServiceTest {

    private static final String TEST_SECRET = "test-secret-key-must-be-at-least-256-bits-long-for-hmac-sha";
    private static final long TEST_EXPIRE_SECONDS = 3600L;

    private UserDomainService userDomainService;

    @BeforeEach
    void setUp() {
        userDomainService = new UserDomainService();
        ReflectionTestUtils.setField(userDomainService, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(userDomainService, "expireSeconds", TEST_EXPIRE_SECONDS);
    }

    @Test
    @DisplayName("generateToken 应生成包含 userId、username 和 roles 的 JWT")
    void generateToken_shouldIncludeUserIdUsernameAndRoles() {
        User user = User.builder().id(1L).username("testuser").build();
        List<Long> roleIds = List.of(1L, 2L);

        String token = userDomainService.generateToken(user, roleIds);

        assertNotNull(token);
        // 解析 JWT 验证 claims
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("username"));
        assertEquals("1,2", claims.get("roles"));
    }

    @Test
    @DisplayName("generateToken 角色列表为空时 roles 字段为空字符串")
    void generateToken_emptyRoles_shouldSetEmptyRoles() {
        User user = User.builder().id(2L).username("nobody").build();

        String token = userDomainService.generateToken(user, List.of());

        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        assertEquals("", claims.get("roles"));
    }

    @Test
    @DisplayName("generateToken 角色列表为 null 时 roles 字段为空字符串")
    void generateToken_nullRoles_shouldSetEmptyRoles() {
        User user = User.builder().id(3L).username("nullroles").build();

        String token = userDomainService.generateToken(user, null);

        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        assertEquals("", claims.get("roles"));
    }

    @Test
    @DisplayName("generateToken 应使用配置的过期时间")
    void generateToken_shouldUseConfiguredExpireSeconds() {
        User user = User.builder().id(1L).username("test").build();

        String token = userDomainService.generateToken(user, List.of(1L));

        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        long diff = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        // 允许 1 秒误差
        assertTrue(Math.abs(diff - TEST_EXPIRE_SECONDS * 1000L) < 1000L);
    }

    @Test
    @DisplayName("getExpireSeconds 应返回配置的有效期")
    void getExpireSeconds_shouldReturnConfiguredValue() {
        assertEquals(TEST_EXPIRE_SECONDS, userDomainService.getExpireSeconds());
    }

    @Test
    @DisplayName("parseUserId 应正确解析 JWT 中的用户 ID")
    void parseUserId_shouldReturnCorrectUserId() {
        User user = User.builder().id(42L).username("test").build();
        String token = userDomainService.generateToken(user, List.of(1L));

        Long userId = userDomainService.parseUserId(token);

        assertEquals(42L, userId);
    }

    @Test
    @DisplayName("validateToken 对有效 Token 返回 true")
    void validateToken_validToken_shouldReturnTrue() {
        User user = User.builder().id(1L).username("test").build();
        String token = userDomainService.generateToken(user, List.of(1L));

        assertTrue(userDomainService.validateToken(token));
    }

    @Test
    @DisplayName("validateToken 对无效 Token 返回 false")
    void validateToken_invalidToken_shouldReturnFalse() {
        assertFalse(userDomainService.validateToken("invalid-jwt-token"));
    }

    @Test
    @DisplayName("encodePassword 应生成 BCrypt 编码的密码")
    void encodePassword_shouldReturnBCryptHash() {
        String rawPassword = "myPassword123";

        String encoded = userDomainService.encodePassword(rawPassword);

        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$10$"));
    }

    @Test
    @DisplayName("checkPassword 应正确校验 BCrypt 密码")
    void checkPassword_shouldVerifyBCryptPassword() {
        String rawPassword = "myPassword123";
        String encoded = userDomainService.encodePassword(rawPassword);
        User user = User.builder().password(encoded).build();

        assertTrue(userDomainService.checkPassword(user, rawPassword));
        assertFalse(userDomainService.checkPassword(user, "wrongPassword"));
    }

    @Test
    @DisplayName("createUser 应创建状态为 ENABLED 的用户")
    void createUser_shouldCreateEnabledUser() {
        User user = userDomainService.createUser("newuser", "pass123", "new@test.com");

        assertNotNull(user);
        assertEquals("newuser", user.getUsername());
        assertEquals("new@test.com", user.getEmail());
        assertEquals(UserStatus.ENABLED, user.getStatus());
        assertNotNull(user.getCreateTime());
        assertNotNull(user.getUpdateTime());
        // 密码应被 BCrypt 加密
        assertTrue(user.getPassword().startsWith("$2a$10$"));
    }
}