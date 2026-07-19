package com.demetrius.vellastra.user.application;

import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.common.response.PageResult;
import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.repository.UserRepository;
import com.demetrius.vellastra.user.domain.user.valueobject.UserRole;
import com.demetrius.vellastra.user.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO;
import com.demetrius.vellastra.user.interfaces.dto.in.UserUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: UserApplicationService</p>
 * <p>Description: 用户应用服务，提供用户CRUD、分页查询等功能</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class UserApplicationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据 ID 获取用户详情
     */
    public UserVO getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        return toVO(user);
    }

    /**
     * 分页查询用户列表
     */
    public PageResult<UserVO> listUsers(int current, int size, String keyword, Integer status) {
        List<User> users = userRepository.findPage(current, size, keyword, status);
        long total = userRepository.count(keyword, status);

        return PageResult.of(
                users.stream().map(this::toVO).toList(),
                total,
                current,
                size
        );
    }

    /**
     * 新增用户
     *
     * @param dto 创建用户请求
     * @return 新用户 ID
     */
    @Transactional
    public Long createUser(UserCreateDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new BizException(400, "密码长度至少6位");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .nickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername())
                .avatar(dto.getAvatar())
                .role(UserRole.USER)
                .status(UserStatus.ENABLED)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return user.getId();
    }

    /**
     * 更新用户信息
     *
     * @param id  用户ID
     * @param dto 更新用户请求
     */
    @Transactional
    public void updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 逻辑删除用户
     *
     * @param id 用户ID
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        // 逻辑删除：将用户名加上 _deleted_{id} 后缀避免唯一索引冲突，再标记删除
        user.setUsername(user.getUsername() + "_deleted_" + id);
        user.setStatus(UserStatus.DISABLED);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        userRepository.deleteById(id); // 物理删除由 @TableLogic 处理
    }

    /**
     * 启用/禁用用户
     *
     * @param id     用户ID
     * @param status 1启用 0禁用
     */
    @Transactional
    public void updateStatus(Long id, Integer status) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        user.setStatus(status == 1 ? UserStatus.ENABLED : UserStatus.DISABLED);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus() != null ? user.getStatus().getCode() : null);
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}