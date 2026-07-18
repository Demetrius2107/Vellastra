package com.demetrius.vellastra.auth.infrastructure.persistence;

import com.demetrius.vellastra.auth.domain.role.entity.Role;
import com.demetrius.vellastra.auth.domain.role.repository.RoleRepository;
import com.demetrius.vellastra.auth.infrastructure.persistence.converter.RoleConverter;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.RoleMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.RolePO;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <h3>角色仓储实现</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;
    private final RoleConverter roleConverter;

    public RoleRepositoryImpl(RoleMapper roleMapper, RoleConverter roleConverter) {
        this.roleMapper = roleMapper;
        this.roleConverter = roleConverter;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(roleMapper.selectById(id)).map(roleConverter::toDomain);
    }

    @Override
    public Role getById(Long id) {
        return findById(id).orElse(null);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(null).stream()
                .map(roleConverter::toDomain).toList();
    }

    @Override
    public List<Role> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return roleMapper.selectBatchIds(ids).stream()
                .map(roleConverter::toDomain).toList();
    }

    @Override
    public void save(Role role) {
        RolePO po = roleConverter.toPO(role);
        if (po.getId() == null) {
            roleMapper.insert(po);
            role.setId(po.getId());
        } else {
            roleMapper.updateById(po);
        }
    }

    @Override
    public void delete(Long id) {
        roleMapper.deleteById(id);
    }
}