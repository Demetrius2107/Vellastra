package com.demetrius.vellastra.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <h3>分页查询基类</h3>
 *
 * <p>所有分页查询接口的入参统一继承此类，确保分页参数命名一致。
 * 各模块的查询 DTO 可继承后添加自己的筛选条件字段。</p>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * @Data
 * public class ArticlePageQuery extends BasePageQuery {
 *     private Long categoryId;
 *     private String keyword;
 *     private Long authorId;
 * }
 * }</pre>
 * </p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-07-18
 */
@Data
public class BasePageQuery {

    @Schema(description = "页码", defaultValue = "1", example = "1")
    private long current = 1;

    @Schema(description = "每页条数", defaultValue = "10", example = "10")
    private long size = 10;

    @Schema(description = "排序字段（如 create_time）")
    private String sortField;

    @Schema(description = "排序方向：ASC / DESC", defaultValue = "DESC", example = "DESC")
    private String sortOrder = "DESC";
}