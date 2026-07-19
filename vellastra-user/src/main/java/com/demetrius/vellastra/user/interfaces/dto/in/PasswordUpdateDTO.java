package com.demetrius.vellastra.user.interfaces.dto.in;

import lombok.Data;

/**
 * <p>Title: PasswordUpdateDTO</p>
 * <p>Description: 修改密码请求 DTO</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
public class PasswordUpdateDTO {
    /** 用户ID（可从请求头 X-User-Id 获取，也支持显式传入） */
    private Long userId;
    /** 旧密码 */
    private String oldPassword;
    /** 新密码 */
    private String newPassword;
}