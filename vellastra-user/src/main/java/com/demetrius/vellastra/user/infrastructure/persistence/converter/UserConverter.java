package com.demetrius.vellastra.user.infrastructure.persistence.converter;

import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.user.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: 用户转换器</p>
 * <p>Description: 用户领域实体与数据库PO对象双向转换器，完成值对象与存储编码互转</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Component
public class UserConverter {

    /**
     * PO对象转换为领域实体User
     *
     * @param po 数据库持久化对象
     * @return 业务领域用户聚合根实体
     */
    public User toDomain(UserPO po) {
        if (po == null) {
            return null;
        }
        return User.builder()
                .id(po.getId())
                .username(po.getUsername())
                .email(po.getEmail())
                .nickname(po.getNickname())
                .avatar(po.getAvatar())
                .status(UserStatus.of(po.getStatus()))
                .lastLoginTime(po.getLastLoginTime())
                .lastLoginIp(po.getLastLoginIp())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    /**
     * 领域实体User转换为数据库PO对象
     *
     * @param domain 业务领域用户实体
     * @return 适配数据库映射的PO持久对象
     */
    public UserPO toPO(User domain) {
        if (domain == null) {
            return null;
        }
        UserPO po = new UserPO();
        po.setId(domain.getId());
        po.setUsername(domain.getUsername());
        po.setEmail(domain.getEmail());
        po.setNickname(domain.getNickname());
        po.setAvatar(domain.getAvatar());
        po.setStatus(domain.getStatus().getCode());
        po.setLastLoginIp(domain.getLastLoginIp());
        po.setLastLoginTime(domain.getLastLoginTime());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
