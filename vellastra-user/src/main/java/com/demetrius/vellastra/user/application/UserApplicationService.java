package com.demetrius.vellastra.user.application;

import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.repository.UserRepository;
import com.demetrius.vellastra.user.interfaces.dto.in.UserUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

/**
 * <p>Title: 用户应用服务</p>
 * <p>Description: 用户模块应用层服务，封装用户查询、更新业务逻辑，完成领域实体与前端VO转换</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Service
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据用户ID查询用户信息
     * @param id 用户主键ID
     * @return 用户前端展示VO
     * @throws BizException 用户不存在时抛出 USER_NOT_FOUND 异常
     */
    public UserVO getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        return toVO(user);
    }

    /**
     * 更新用户基础信息（昵称、邮箱、头像）
     * @param id 待更新用户ID
     * @param dto 待更新用户参数
     * @throws BizException 用户不存在时抛出 USER_NOT_FOUND 异常
     */
    public void updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        userRepository.save(user);
    }

    /**
     * 领域实体 User 转换为对外展示VO
     * @param user 数据库领域用户实体
     * @return 前端传输对象 UserVO
     */
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus().getCode());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
