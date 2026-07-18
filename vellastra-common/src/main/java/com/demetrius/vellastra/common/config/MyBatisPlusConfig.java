package com.demetrius.vellastra.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>MyBatis-Plus 全局配置</h3>
 *
 * <p>配置 MyBatis-Plus 插件，各模块直接复用此配置。</p>
 *
 * <p><b>当前配置的插件：</b>
 * <ul>
 *   <li>{@link PaginationInnerInterceptor} — 分页拦截器，自动生成 COUNT 查询 + 分页 SQL</li>
 * </ul>
 * </p>
 *
 * <p><b>后续可扩展的插件（按需添加）：</b>
 * <ul>
 *   <li>{@code OptimisticLockerInnerInterceptor} — 乐观锁插件</li>
 *   <li>{@code IllegalSQLInnerInterceptor} — SQL 性能规范拦截器</li>
 *   <li>{@code BlockAttackInnerInterceptor} — 防止全表更新/删除</li>
 * </ul>
 * </p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-07-18
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器链
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件（数据库类型为 MySQL）
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 超过最大页数时返回第一页，而非抛异常
        pagination.setOverflow(true);
        // 单页最大 500 条，防止恶意查询
        pagination.setMaxLimit(500L);
        interceptor.addInnerInterceptor(pagination);

        return interceptor;
    }
}