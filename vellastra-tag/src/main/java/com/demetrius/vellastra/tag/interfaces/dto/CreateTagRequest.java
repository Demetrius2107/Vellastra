package com.demetrius.vellastra.tag.interfaces.dto;

import lombok.Data;

/**
 * <p>Title: CreateTagRequest</p>
 * <p>Description: 创建标签请求 DTO</p>
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
public class CreateTagRequest {
    /** 标签名称 */
    private String name;
    /** 标签别名 */
    private String slug;
}