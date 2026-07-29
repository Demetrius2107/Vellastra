package com.demetrius.vellastra.user.interfaces.dto.in;

import lombok.Data;

/**
 * <p>Title: UserInfoUpdateDTO</p>
 * <p>Description: 当前用户信息更新请求 DTO</p>
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
public class UserInfoUpdateDTO {
    /** 昵称 */
    private String nickname;
    /** 头像URL */
    private String avatar;
    /** 个人简介 */
    private String bio;
}