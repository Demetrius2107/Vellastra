package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

/**
 * <p>Title: RefreshRequest</p>
 * <p>Description: 刷新token请求 DTO</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-05
 * @updateTime 2026-07-05
 */
@Data
public class RefreshRequest {

    private String token;
}
