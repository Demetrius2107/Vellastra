package com.demetrius.vellastra.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: LoginAttemptService</p>
 * <p>Description: 登录失败次数限制服务，防止暴力破解</p>
 * <p>项目名称: Vellastra</p>
 *
 * <p>策略：连续失败 N 次后锁定账号 M 分钟。
 * 使用内存存储，后续可替换为 Redis 实现。</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Service
public class LoginAttemptService {

    /** 最大失败次数 */
    @Value("${auth.login.max-attempts:5}")
    private int maxAttempts;

    /** 锁定时间（分钟） */
    @Value("${auth.login.lock-minutes:15}")
    private int lockMinutes;

    /** 失败次数缓存：username -> 失败次数 */
    private final ConcurrentHashMap<String, AtomicInteger> attemptsCache = new ConcurrentHashMap<>();

    /** 锁定时间缓存：username -> 锁定截止时间戳（毫秒） */
    private final ConcurrentHashMap<String, Long> lockCache = new ConcurrentHashMap<>();

    /**
     * 登录失败：增加失败次数，如果达到上限则锁定
     *
     * @param username 登录账号
     */
    public void loginFailed(String username) {
        AtomicInteger attempts = attemptsCache.computeIfAbsent(username, k -> new AtomicInteger(0));
        int count = attempts.incrementAndGet();
        log.warn("登录失败: username={}, 失败次数={}/{}", username, count, maxAttempts);

        if (count >= maxAttempts) {
            long lockUntil = System.currentTimeMillis() + lockMinutes * 60 * 1000L;
            lockCache.put(username, lockUntil);
            log.warn("账号已被锁定: username={}, 锁定时长={}分钟, 解锁时间={}",
                    username, lockMinutes, new java.util.Date(lockUntil));
        }
    }

    /**
     * 登录成功：清除失败次数和锁定状态
     *
     * @param username 登录账号
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockCache.remove(username);
    }

    /**
     * 检查账号是否被锁定
     *
     * @param username 登录账号
     * @return true=已锁定
     */
    public boolean isLocked(String username) {
        Long lockUntil = lockCache.get(username);
        if (lockUntil == null) {
            return false;
        }
        // 锁定时间已过，自动解锁
        if (System.currentTimeMillis() > lockUntil) {
            lockCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }

    /**
     * 获取锁定剩余时间（秒），用于提示
     *
     * @param username 登录账号
     * @return 剩余秒数，0=未锁定
     */
    public long getRemainingLockSeconds(String username) {
        Long lockUntil = lockCache.get(username);
        if (lockUntil == null) {
            return 0;
        }
        long remaining = (lockUntil - System.currentTimeMillis()) / 1000;
        return Math.max(remaining, 0);
    }

    /**
     * 获取剩余尝试次数
     *
     * @param username 登录账号
     * @return 剩余次数
     */
    public int getRemainingAttempts(String username) {
        AtomicInteger attempts = attemptsCache.get(username);
        if (attempts == null) {
            return maxAttempts;
        }
        return Math.max(maxAttempts - attempts.get(), 0);
    }
}