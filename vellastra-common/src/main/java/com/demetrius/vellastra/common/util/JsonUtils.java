package com.demetrius.vellastra.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <h3>JSON 工具类</h3>
 *
 * <p>基于 Jackson 的 {@link ObjectMapper} 封装，提供简洁的序列化/反序列化操作。</p>
 *
 * <p>项目中同时存在 Hutool 的 {@code JSONUtil} 和 Jackson，统一封装此工具类避免直接依赖任一方，
 * 后续如需切换 JSON 库（如换为 Fastjson2 / Gson），只需修改此文件即可。</p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-07-18
 */
@Slf4j
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 对象 → JSON 字符串
     *
     * @param object 任意对象
     * @return JSON 字符串，失败时返回 null
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败: object={}", object.getClass().getName(), e);
            return null;
        }
    }

    /**
     * JSON 字符串 → 指定类型的对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 反序列化后的对象，失败时返回 null
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON 反序列化失败: type={}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * JSON 字符串 → 泛型 List（如 {@code List<ArticleVO>}）
     *
     * <p>使用示例：
     * <pre>{@code
     * List<ArticleVO> list = JsonUtils.toList(json, ArticleVO.class);
     * }</pre>
     * </p>
     *
     * @param json  JSON 字符串
     * @param clazz 列表元素类型
     * @param <T>   元素泛型
     * @return 反序列化后的列表，失败时返回空列表
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("JSON 反序列化失败: type=List<{}>", clazz.getName(), e);
            return Collections.emptyList();
        }
    }

    /**
     * JSON 字符串 → {@code Map<String, Object>}
     *
     * @param json JSON 字符串
     * @return 反序列化后的 Map，失败时返回空 Map
     */
    public static Map<String, Object> toMap(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON 反序列化失败: type=Map<String, Object>", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 获取底层 ObjectMapper 实例（供高级定制使用）
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}