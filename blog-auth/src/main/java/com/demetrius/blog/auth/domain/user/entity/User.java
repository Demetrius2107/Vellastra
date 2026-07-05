package com.demetrius.blog.auth.domain.user.entity;

import com.demetrius.blog.auth.domain.user.valueobject.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: User</p>
 * <p>Description: 用户领域实体（auth 模块）</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {


    // ID
    private Long id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 邮箱
    private String email;

    // 昵称
    private String nickname;

    // 头像
    private String avatar;

    // 状态
    private UserStatus status;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 判断是否启用
    public boolean isEnabled() {
        return this.status == UserStatus.ENABLED;
    }

    // 禁用
    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    // 启用
    public void enable() {
        this.status = UserStatus.ENABLED;
    }
}
