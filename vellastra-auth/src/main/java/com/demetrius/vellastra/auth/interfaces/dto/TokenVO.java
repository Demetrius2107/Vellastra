package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: TokenVO</p>
 * <p>Description: token 视图对象</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {

    private String token;
    private Long expireIn;
}
