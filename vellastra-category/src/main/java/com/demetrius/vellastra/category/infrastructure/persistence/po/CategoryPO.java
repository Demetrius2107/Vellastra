package com.demetrius.vellastra.category.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: CategoryPO</p>
 * <p>Description: 分类持久化对象</p>
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
@TableName("t_category")
public class CategoryPO {

    /** 分类ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** URL别名 */
    private String slug;

    /** 分类描述 */
    private String description;

    /** 图标URL */
    private String icon;

    /** 父分类ID（顶级为0） */
    private Long parentId;

    /** 排序值（越小越靠前） */
    private Integer sortOrder;

    /** 文章数 */
    private Integer articleCount;

    /** 状态：0禁用 1正常 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
