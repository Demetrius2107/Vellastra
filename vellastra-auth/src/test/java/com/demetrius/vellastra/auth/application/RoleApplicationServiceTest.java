package com.demetrius.vellastra.auth.application;

import com.demetrius.vellastra.auth.domain.role.entity.Role;
import com.demetrius.vellastra.auth.domain.role.repository.RoleRepository;
import com.demetrius.vellastra.auth.interfaces.dto.CreateRoleRequest;
import com.demetrius.vellastra.auth.interfaces.dto.RoleVO;
import com.demetrius.vellastra.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RoleApplicationService}
 */
@ExtendWith(MockitoExtension.class)
class RoleApplicationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    private RoleApplicationService roleApplicationService;

    @BeforeEach
    void setUp() {
        roleApplicationService = new RoleApplicationService(roleRepository);
    }

    @Test
    @DisplayName("createRole 应保存角色并返回 ID")
    void createRole_shouldSave() {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setRoleName("测试角色");
        request.setRoleCode("TEST");
        request.setDescription("测试用");

        doAnswer(invocation -> {
            Role r = invocation.getArgument(0);
            r.setId(1L);
            return null;
        }).when(roleRepository).save(any());

        Long id = roleApplicationService.createRole(request);
        assertEquals(1L, id);
    }

    @Test
    @DisplayName("getRoleById 不存在时抛出异常")
    void getRoleById_notFound_shouldThrow() {
        when(roleRepository.getById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> roleApplicationService.getRoleById(99L));
    }

    @Test
    @DisplayName("listRoles 返回角色列表")
    void listRoles_shouldReturnList() {
        Role role = Role.builder().id(1L).roleName("管理员").roleCode("ADMIN").build();
        when(roleRepository.findAll()).thenReturn(java.util.List.of(role));

        var list = roleApplicationService.listRoles();
        assertEquals(1, list.size());
        assertEquals("管理员", list.get(0).getRoleName());
    }
}