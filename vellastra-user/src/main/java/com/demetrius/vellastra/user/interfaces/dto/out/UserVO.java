package com.demetrius.vellastra.user.interfaces.dto.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: 用户视图对象</p>
 * <p>Description: 用户前端展示视图对象VO</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class UserVO {

    /** 用户ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 邮箱 */
    private String email;
    /** 昵称 */
    private String nickname;
    /** 头像URL */
    private String avatar;
    /** 状态（1禁用 0启用） */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
}
