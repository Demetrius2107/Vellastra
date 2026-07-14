package com.demetrius.vellastra.user.interfaces.dto.in;

import lombok.Data;

/**
 * <p>Title: 更新用户数据传输对象</p>
 * <p>Description: 更新用户信息请求参数DTO</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class UserUpdateDTO {
    /** 昵称 */
    private String nickname;
    /** 邮箱 */
    private String email;
    /** 头像URL */
    private String avatar;
}