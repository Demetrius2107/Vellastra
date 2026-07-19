package com.demetrius.vellastra.auth.application;

import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.LoginLogMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.LoginLogPO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>Title: LoginLogService</p>
 * <p>Description: 登录日志服务，记录每次登录成功/失败到 t_login_log 表</p>
 * <p>项目名称: Vellastra</p>
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
public class LoginLogService {

    private final LoginLogMapper loginLogMapper;

    public LoginLogService(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    /**
     * 记录登录日志
     *
     * @param userId   用户ID（失败时可能为 null）
     * @param username 登录账号
     * @param success  是否成功
     * @param message  提示消息
     * @param request  HTTP 请求（用于提取 IP、User-Agent 等）
     */
    public void record(Long userId, String username, boolean success, String message, HttpServletRequest request) {
        LoginLogPO po = new LoginLogPO();
        po.setUserId(userId);
        po.setUsername(username);
        po.setLoginType(1); // 账号密码登录
        po.setStatus(success ? 1 : 0);
        po.setMessage(message);
        po.setCreateTime(LocalDateTime.now());

        if (request != null) {
            po.setIpAddress(getClientIp(request));
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null) {
                po.setBrowser(parseBrowser(userAgent));
                po.setOs(parseOs(userAgent));
            }
        }

        loginLogMapper.insert(po);
        log.debug("登录日志已记录: username={}, success={}, ip={}", username, success, po.getIpAddress());
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 从 User-Agent 解析浏览器名称
     */
    private String parseBrowser(String userAgent) {
        if (userAgent == null) return null;
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg")) return "Edge";
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari")) return "Safari";
        return "Other";
    }

    /**
     * 从 User-Agent 解析操作系统
     */
    private String parseOs(String userAgent) {
        if (userAgent == null) return null;
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("mac")) return "macOS";
        if (ua.contains("linux")) return "Linux";
        if (ua.contains("android")) return "Android";
        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        return "Other";
    }
}