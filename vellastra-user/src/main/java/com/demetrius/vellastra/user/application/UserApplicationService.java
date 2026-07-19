package com.demetrius.vellastra.user.application;

import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.common.response.PageResult;
import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.repository.UserRepository;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>Title: UserApplicationService</p>
 * <p>Description: 用户应用服务，提供用户分页查询、详情查询等功能</p>
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

    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据 ID 获取用户详情
     *
     * @param id 用户ID
     * @return 用户视图对象
     * @throws com.demetrius.vellastra.common.exception.BizException 用户不存在时抛出
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
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @param status  状态筛选
     * @return 分页结果
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
     * 将领域实体转换为视图对象
     */
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