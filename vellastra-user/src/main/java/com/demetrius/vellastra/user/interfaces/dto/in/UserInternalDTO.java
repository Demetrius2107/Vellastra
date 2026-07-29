package com.demetrius.vellastra.user.interfaces.dto.in;

import lombok.Data;

/**
 * <p>Title: UserInternalDTO</p>
 * <p>Description: 用户内部 API 返回 DTO，供 Feign 客户端调用</p>
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
public class UserInternalDTO {
    /** 用户ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickname;
    /** 头像URL */
    private String avatar;
    /** 邮箱 */
    private String email;
}