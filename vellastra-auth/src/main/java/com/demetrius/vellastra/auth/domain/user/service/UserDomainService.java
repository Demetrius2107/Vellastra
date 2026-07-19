package com.demetrius.vellastra.auth.domain.user.service;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.common.service.TokenBlackListService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>Title: UserDomainService</p>
 * <p>Description: 用户领域服务，负责密码校验、JWT生成/解析/校验、用户创建等核心逻辑</p>
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
public class UserDomainService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final TokenBlackListService tokenBlackListService;

    @Value("${jwt.secret:demetrius-vellastra-secret-key-2024-must-be-long-enough}")
    private String jwtSecret;

    @Value("${jwt.expire-seconds:7200}")
    private long expireSeconds;

    public UserDomainService(TokenBlackListService tokenBlackListService) {
        this.tokenBlackListService = tokenBlackListService;
    }

    /**
     * 校验密码
     *
     * @param user        用户实体
     * @param rawPassword 明文密码
     * @return true=密码正确
     */
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * 密码编码（使用 BCrypt）
     *
     * @param rawPassword 明文密码
     * @return BCrypt 编码后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 校验密码强度
     *
     * <p>规则：至少8位，至少包含1个字母和1个数字。</p>
     *
     * @param password 明文密码
     * @throws BizException 密码强度不足时抛出
     */
    public void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new BizException(ErrorCode.USER_PASSWORD_WEAK.getCode(),
                    "密码长度至少8位");
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        if (!hasLetter || !hasDigit) {
            throw new BizException(ErrorCode.USER_PASSWORD_WEAK.getCode(),
                    "密码必须包含字母和数字");
        }
    }

    /**
     * 获取 Token 有效期（秒）
     */
    public long getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * 解析 Token 的过期时间戳（毫秒）
     */
    public long getExpireAtMillis(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration().getTime();
    }

    /**
     * 生成 JWT token
     *
     * @param user    用户实体
     * @param roleIds 用户角色 ID 列表
     * @return JWT token 字符串
     */
    public String generateToken(User user, List<Long> roleIds) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        long nowMillis = System.currentTimeMillis();
        String rolesStr = roleIds == null || roleIds.isEmpty()
                ? ""
                : roleIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("roles", rolesStr)
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(nowMillis + expireSeconds * 1000L))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token 中的用户 ID
     */
    public Long parseUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 校验 token 是否有效（含黑名单检查）
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            // 检查是否在黑名单中
            if (tokenBlackListService.isBlacklisted(token)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public User createUser(String username, String password, String email) {
        return User.builder()
                .username(username)
                .password(encodePassword(password))
                .email(email)
                .nickname(username)
                .status(UserStatus.ENABLED)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }
}
