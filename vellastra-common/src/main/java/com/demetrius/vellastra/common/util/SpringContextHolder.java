package com.demetrius.vellastra.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <h3>Spring 上下文持有器</h3>
 *
 * <p>静态持有 Spring {@link ApplicationContext}，在非 Spring Bean 中获取 Bean 实例。</p>
 *
 * <p><b>典型使用场景：</b>
 * <ul>
 *   <li>在 Hutool 的 {@code Singleton} 或工厂类中获取 Spring Bean</li>
 *   <li>在 {@code Dubbo Filter} 或自定义线程池中获取 Bean</li>
 *   <li>在静态方法中获取配置值</li>
 * </ul>
 * </p>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * UserService userService = SpringContextHolder.getBean(UserService.class);
 * String profile = SpringContextHolder.getProperty("spring.profiles.active");
 * }</pre>
 * </p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-07-18
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        log.debug("SpringContextHolder initialized: applicationContext={}", context.getDisplayName());
    }

    /**
     * 获取 ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 按类型获取 Bean
     *
     * @param clazz Bean 类型
     * @param <T>   泛型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        assertContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 按名称 + 类型获取 Bean
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   泛型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        assertContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取配置属性值
     *
     * @param key 配置键
     * @return 配置值，未配置时返回 null
     */
    public static String getProperty(String key) {
        assertContext();
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取配置属性值（带默认值）
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值，未配置时返回 defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        assertContext();
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    private static void assertContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("SpringContextHolder 尚未初始化，请确保它被 Spring 扫描到");
        }
    }
}