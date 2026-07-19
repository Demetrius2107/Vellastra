package com.demetrius.vellastra.auth.application;

import com.demetrius.vellastra.auth.domain.role.entity.Role;
import com.demetrius.vellastra.auth.domain.role.repository.RoleRepository;
import com.demetrius.vellastra.auth.interfaces.dto.RoleVO;
import com.demetrius.vellastra.auth.interfaces.dto.CreateRoleRequest;
import com.demetrius.vellastra.auth.interfaces.dto.UpdateRoleRequest;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>Title: RoleApplicationService</p>
 * <p>Description: 角色应用服务，负责角色的 CRUD 和菜单权限分配</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class RoleApplicationService {

    private final RoleRepository roleRepository;

    public RoleApplicationService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * 获取所有角色列表
     *
     * @return 角色视图对象列表
     */
    public List<RoleVO> listRoles() {
        return roleRepository.findAll().stream().map(this::toVO).toList();
    }

    /**
     * 根据 ID 获取角色详情
     *
     * @param id 角色ID
     * @return 角色视图对象
     */
    public RoleVO getRoleById(Long id) {
        Role role = roleRepository.getById(id);
        if (role == null) throw ErrorCode.COMMENT_NOT_FOUND.toException();
        return toVO(role);
    }

    /**
     * 创建角色
     *
     * @param request 创建角色请求
     * @return 新角色 ID
     */
    @Transactional
    public Long createRole(CreateRoleRequest request) {
        Role role = Role.builder()
                .roleName(request.getRoleName())
                .roleCode(request.getRoleCode())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(1)
                .build();
        role.initCreateTime();
        roleRepository.save(role);
        return role.getId();
    }

    /**
     * 更新角色信息
     *
     * @param id      角色ID
     * @param request 更新角色请求
     */
    @Transactional
    public void updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.getById(id);
        if (role == null) throw ErrorCode.COMMENT_NOT_FOUND.toException();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        if (request.getSortOrder() != null) role.setSortOrder(request.getSortOrder());
        if (request.getStatus() != null) role.setStatus(request.getStatus());
        role.updateTime();
        roleRepository.save(role);
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.getById(id);
        if (role == null) throw ErrorCode.COMMENT_NOT_FOUND.toException();
        roleRepository.delete(id);
    }

    /**
     * 将角色实体转换为视图对象
     *
     * @param role 角色实体
     * @return 角色视图对象
     */
    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDescription(role.getDescription());
        vo.setSortOrder(role.getSortOrder());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}