package com.demetrius.vellastra.auth.infrastructure.persistence.converter;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Component;


/**
 * <p>Title: UserConverter</p>
 * <p>Description: 用户对象转换器（PO <-> Domain）</p>
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
@Component
public class UserConverter {

    public User toDomain(UserPO po) {
        if (po == null) return null;
        return User.builder()
                .id(po.getId())
                .username(po.getUsername())
                .password(po.getPassword())
                .email(po.getEmail())
                .nickname(po.getNickname())
                .avatar(po.getAvatar())
                .status(UserStatus.of(po.getStatus()))
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    public UserPO toPO(User domain) {
        if (domain == null) return null;
        UserPO po = new UserPO();
        po.setId(domain.getId());
        po.setUsername(domain.getUsername());
        po.setPassword(domain.getPassword());
        po.setEmail(domain.getEmail());
        po.setNickname(domain.getNickname());
        po.setAvatar(domain.getAvatar());
        po.setStatus(domain.getStatus().getCode());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
