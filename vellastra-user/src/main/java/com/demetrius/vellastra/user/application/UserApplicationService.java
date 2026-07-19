package com.demetrius.vellastra.user.application;

import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.common.response.PageResult;
import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.repository.UserRepository;
import com.demetrius.vellastra.user.domain.user.valueobject.UserRole;
import com.demetrius.vellastra.user.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO;
import com.demetrius.vellastra.user.interfaces.dto.in.PasswordUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.in.UserUpdateDTO;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: UserApplicationService</p>
 * <p>Description: 用户应用服务，提供用户CRUD、密码重置/修改、分页查询等功能</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
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
        if (id == null) {
            throw new BizException(400, "用户ID不能为空");
        }
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
        if (current < 1) current = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100; // 防止一次拉取过多数据

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
        // ===== 参数校验 =====
        if (dto == null) {
            throw new BizException(400, "请求参数不能为空");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new BizException(400, "用户名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new BizException(400, "密码长度至少6位");
        }

        // ===== 构建用户实体 =====
        User user = User.builder()
                .username(dto.getUsername().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail() != null ? dto.getEmail().trim() : null)
                .nickname(dto.getNickname() != null ? dto.getNickname().trim() : dto.getUsername().trim())
                .avatar(dto.getAvatar())
                .role(UserRole.USER)
                .status(UserStatus.ENABLED)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // ===== 保存 =====
        try {
            userRepository.save(user);
            log.info("新增用户成功: userId={}, username={}", user.getId(), user.getUsername());
            return user.getId();
        } catch (DataIntegrityViolationException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("username")) {
                throw new BizException(400, "用户名已存在");
            }
            if (msg != null && msg.contains("email")) {
                throw new BizException(400, "邮箱已被使用");
            }
            throw new BizException(400, "数据冲突，请检查用户名和邮箱是否唯一");
        }
    }

    /**
     * 更新用户信息
     *
     * @param id  用户ID
     * @param dto 更新用户请求
     */
    @Transactional
    public void updateUser(Long id, UserUpdateDTO dto) {
        if (id == null) {
            throw new BizException(400, "用户ID不能为空");
        }
        if (dto == null) {
            throw new BizException(400, "请求参数不能为空");
        }
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        if (dto.getNickname() != null) user.setNickname(dto.getNickname().trim());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail().trim());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        log.info("更新用户成功: userId={}", id);
    }

    /**
     * 逻辑删除用户
     *
     * @param id 用户ID
     */
    @Transactional
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BizException(400, "用户ID不能为空");
        }
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        // 逻辑删除：将用户名加上 _deleted_{id} 后缀避免唯一索引冲突，再标记删除
        String baseName = user.getUsername() != null ? user.getUsername() : "user";
        user.setUsername(baseName + "_deleted_" + id);
        user.setStatus(UserStatus.DISABLED);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        userRepository.deleteById(id); // @TableLogic 自动处理逻辑删除
        log.info("删除用户成功: userId={}", id);
    }

    /**
     * 启用/禁用用户
     *
     * @param id     用户ID
     * @param status 1启用 0禁用
     */
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (id == null) {
            throw new BizException(400, "用户ID不能为空");
        }
        if (status == null) {
            throw new BizException(400, "状态不能为空");
        }
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        user.setStatus(status == 1 ? UserStatus.ENABLED : UserStatus.DISABLED);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        log.info("{}用户成功: userId={}", status == 1 ? "启用" : "禁用", id);
    }

    /**
     * 管理员重置密码
     *
     * <p>将用户密码重置为 "123456" 的 BCrypt 加密值。</p>
     *
     * @param id 用户ID
     */
    @Transactional
    public void resetPassword(Long id) {
        if (id == null) {
            throw new BizException(400, "用户ID不能为空");
        }
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        log.info("重置密码成功: userId={}", id);
    }

    /**
     * 用户自助修改密码
     *
     * <p>校验旧密码正确后，更新为新密码。新密码长度至少6位。</p>
     *
     * @param id  用户ID
     * @param dto 修改密码请求（旧密码 + 新密码）
     */
    @Transactional
    public void changePassword(Long id, PasswordUpdateDTO dto) {
        if (id == null) {
            throw new BizException(400, "用户ID不能为空");
        }
        if (dto == null) {
            throw new BizException(400, "请求参数不能为空");
        }
        User user = userRepository.findById(id);
        if (user == null) {
            throw ErrorCode.USER_NOT_FOUND.toException();
        }
        // 校验旧密码
        if (dto.getOldPassword() == null || dto.getOldPassword().isEmpty()) {
            throw new BizException(400, "旧密码不能为空");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BizException(400, "旧密码错误");
        }
        // 校验新密码强度
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new BizException(400, "新密码长度至少6位");
        }
        // 更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        log.info("修改密码成功: userId={}", id);
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