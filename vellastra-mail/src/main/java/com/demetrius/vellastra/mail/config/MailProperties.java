package com.demetrius.vellastra.mail.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
