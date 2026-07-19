package com.demetrius.vellastra.auth.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: RegisterRequest</p>
 * <p>Description: 注册请求 DTO</p>
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
public class RegisterRequest {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 明文密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 邮箱 */
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
