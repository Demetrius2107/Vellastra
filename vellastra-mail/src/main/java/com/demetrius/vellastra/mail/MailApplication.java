package com.demetrius.vellastra.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>Title: MailApplication</p>
 * <p>Description: 邮件服务启动类</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@SpringBootApplication
@EnableAsync
public class MailApplication {
    public static void main(String[] args) { SpringApplication.run(MailApplication.class, args); }
}
