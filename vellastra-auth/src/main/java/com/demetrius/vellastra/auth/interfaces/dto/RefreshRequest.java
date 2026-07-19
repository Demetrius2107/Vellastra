package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

/**
 * <p>Title: RefreshRequest</p>
 * <p>Description: 刷新 token 请求 DTO</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-05
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
public class RefreshRequest {

    /** 当前有效的 JWT token（尚未过期） */
    private String token;
}
