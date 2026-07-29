package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

/**
 * <p>Title: UserDTO</p>
 * <p>Description: 用户基本信息 DTO（Feign 远程调用返回）</p>
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
public class UserDTO {
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