package com.demetrius.vellastra.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: TokenBlackListService</p>
 * <p>Description: Token 黑名单服务，用于登出后使 Token 失效</p>
 * <p>项目名称: Vellastra</p>
 *
 * <p>使用内存 {@link ConcurrentHashMap} 存储已登出的 Token 及过期时间。
 * 后续可替换为 Redis 实现。</p>
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
public class TokenBlackListService {

    /** 黑名单存储：token -> 过期时间戳（毫秒） */
    private final ConcurrentHashMap<String, Long> blackList = new ConcurrentHashMap<>();

    /**
     * 将 Token 加入黑名单
     *
     * @param token          要失效的 Token
     * @param expireAtMillis Token 的过期时间戳（毫秒），用于自动清理
     */
    public void add(String token, long expireAtMillis) {
        blackList.put(token, expireAtMillis);
        log.debug("Token 已加入黑名单: {}...", truncate(token));
    }

    /**
     * 判断 Token 是否在黑名单中
     *
     * @param token 待校验的 Token
     * @return true=已登出/失效
     */
    public boolean isBlacklisted(String token) {
        Long expireAt = blackList.get(token);
        if (expireAt == null) {
            return false;
        }
        // 如果 Token 已过期，则从黑名单中清理
        if (System.currentTimeMillis() > expireAt) {
            blackList.remove(token);
            return false;
        }
        return true;
    }

    /**
     * 定期清理过期的黑名单条目（可由调度任务调用）
     */
    public void cleanExpired() {
        long now = System.currentTimeMillis();
        blackList.entrySet().removeIf(entry -> now > entry.getValue());
        log.debug("黑名单清理完成，当前大小: {}", blackList.size());
    }

    private String truncate(String token) {
        return token != null && token.length() > 20
                ? token.substring(0, 20) + "..."
                : token;
    }
}