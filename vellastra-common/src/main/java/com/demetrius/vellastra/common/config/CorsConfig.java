package com.demetrius.vellastra.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * <h3>跨域配置（CORS）</h3>
 *
 * <p>配置全局跨域访问规则，允许前端跨域调用后端接口。</p>
 *
 * <p><b>说明：</b>如果使用了网关（Gateway），网关层也应配置跨域，两者二选一即可。
 * 本配置作为各微服务的兜底跨域策略。</p>
 *
 * @author wanqiu
 * @version 1.0
 * @since 2026-07-18
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许跨域的域名（生产环境应替换为具体域名）
        config.addAllowedOriginPattern("*");
        // 允许携带 Cookie
        config.setAllowCredentials(true);
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有 HTTP 方法
        config.addAllowedMethod("*");
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}