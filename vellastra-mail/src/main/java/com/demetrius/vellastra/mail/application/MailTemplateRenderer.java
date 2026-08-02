package com.demetrius.vellastra.mail.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: MailTemplateRenderer</p>
 * <p>Description: 邮件模板渲染器，支持 {{var}} 变量替换与链接生成</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Component
public class MailTemplateRenderer {

    /** 变量占位符：{{ variableName }} */
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*\\}\\}");

    /**
     * 渲染模板：将 {{var}} 替换为变量值
     *
     * @param template 模板内容
     * @param variables 变量 Map
     * @return 渲染后的内容
     */
    public String render(String template, Map<String, Object> variables) {
        if (template == null) return null;
        if (variables == null || variables.isEmpty()) return template;

        Matcher matcher = VAR_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String varName = matcher.group(1);
            Object value = variables.get(varName);
            String replacement = value != null ? String.valueOf(value) : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 生成订阅确认链接
     *
     * @param baseUrl 站点基础 URL
     * @param token   订阅确认 token
     * @return 完整确认链接
     */
    public String buildConfirmLink(String baseUrl, String token) {
        return baseUrl + "/mail/confirm?token=" + token;
    }

    /**
     * 生成退订链接
     *
     * @param baseUrl 站点基础 URL
     * @param token   订阅者 token
     * @return 完整退订链接
     */
    public String buildUnsubscribeLink(String baseUrl, String token) {
        return baseUrl + "/mail/unsubscribe?token=" + token;
    }
}
