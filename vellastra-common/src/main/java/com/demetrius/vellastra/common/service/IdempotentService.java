package com.demetrius.vellastra.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: IdempotentService</p>
 * <p>Description: 幂等服务，管理幂等 Token 的生成与校验</p>
 * <p>项目名称: Vellastra</p>
 *
 * <p>使用本地内存存储已使用的 Token，后续可替换为 Redis。</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Service
public class IdempotentService {

    private final ConcurrentHashMap<String, Long> tokenStore = new ConcurrentHashMap<>();

    /**
     * 生成幂等 Token
     *
     * @return 唯一 Token 字符串
     */
    public String generateToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, System.currentTimeMillis());
        log.debug("幂等 Token 已生成: {}", token);
        return token;
    }

    /**
     * 校验并消费 Token
     *
     * @param token      幂等 Token
     * @param ttlSeconds Token 有效期（超过此时间的 Token 允许重放）
     * @return true=Token 有效（首次使用），false=重复提交
     */
    public boolean tryConsume(String token, long ttlSeconds) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        Long createdAt = tokenStore.get(token);
        if (createdAt == null) {
            // Token 不存在（可能是未生成的非法 Token）
            return false;
        }
        // 检查是否超过有效期
        if (System.currentTimeMillis() - createdAt > ttlSeconds * 1000L) {
            // 超过有效期，移除旧 Token，允许重放
            tokenStore.remove(token);
            return false;
        }
        // 移除 Token（消费），后续相同 Token 请求会被拒绝
        tokenStore.remove(token);
        return true;
    }

    /** 清理过期的 Token（由定时任务调用） */
    public void cleanExpired(long ttlSeconds) {
        long now = System.currentTimeMillis();
        tokenStore.entrySet().removeIf(entry -> now - entry.getValue() > ttlSeconds * 1000L);
    }
}