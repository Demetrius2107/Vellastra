package com.demetrius.vellastra.article.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: UpdateArticleRequest</p>
 * <p>Description: 更新文章请求 DTO</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
@Data
public class UpdateArticleRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 汇总
     */
    private String summary;

    /**
     * 封面图
     */
    private String coverImage;

    /**
     * 分类
     */
    private Long categoryId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 标签
     */
    private String tags;
}
