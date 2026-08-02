package com.demetrius.vellastra.mail.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>Title: MailProperties</p>
 * <p>Description: 邮件系统配置属性（发件人、站点链接、限速等）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@Component
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    /** 发件人邮箱 */
    private String from;
    /** 发件人名称 */
    private String fromName = "Vellastra";
    /** 站点基础 URL（用于生成确认/退订链接） */
    private String baseUrl = "http://localhost:8080";
    /** 发送速率限制（每秒） */
    private int sendRateLimitPerSecond = 10;
    /** 退订路径 */
    private String unsubscribePath = "/mail/unsubscribe";
    /** 确认路径 */
    private String confirmPath = "/mail/confirm";
}
