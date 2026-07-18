package com.demetrius.vellastra.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h3>统一分页响应体</h3>
 *
 * <p>所有分页查询接口统一使用此结构返回，与 MyBatis-Plus {@code IPage} 一一对应。</p>
 *
 * <p><b>响应格式：</b>
 * <pre>
 * {
 *   "records": [],   // 当前页数据列表
 *   "total": 100,    // 总记录数
 *   "current": 1,    // 当前页码
 *   "size": 10,      // 每页条数
 *   "pages": 10      // 总页数
 * }
 * </pre>
 * </p>
 *
 * @param <T> 记录类型
 * @author wanqiu
 * @since 1.1
 * @since 2026-05-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 当前页数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 每页条数
     */
    private long size;

    /**
     * 总页数（由 total 和 size 计算得出）
     */
    private long pages;

    /**
     * 创建分页结果（自动计算总页数）
     *
     * @param records 当前页数据
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页条数
     * @param <T>     记录类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        long pages = (total + size - 1) / size;
        return new PageResult<>(records, total, current, size, pages);
    }
}
