package com.demetrius.vellastra.tag.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: TagVO</p>
 * <p>Description: 标签视图对象</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
public class TagVO {
    /** 标签ID */
    private Long id;
    /** 标签名称 */
    private String name;
    /** 标签别名 */
    private String slug;
    /** 使用次数 */
    private Integer articleCount;
    /** 创建时间 */
    private LocalDateTime createTime;
}