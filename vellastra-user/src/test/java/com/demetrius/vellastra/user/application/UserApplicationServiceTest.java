package com.demetrius.vellastra.user.application;

import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.repository.UserRepository;
import com.demetrius.vellastra.user.domain.user.valueobject.UserRole;
import com.demetrius.vellastra.user.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.user.interfaces.dto.out.UserVO;
import com.demetrius.vellastra.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserApplicationService}
 */
@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserApplicationService userApplicationService;

    @BeforeEach
    void setUp() {
        userApplicationService = new UserApplicationService(userRepository);
    }

    @Test
    @DisplayName("getUserById 存在时返回 UserVO")
    void getUserById_existing_shouldReturnVO() {
        User user = User.builder().id(1L).username("test").nickname("Test").role(UserRole.ADMIN).status(UserStatus.ENABLED).build();
        when(userRepository.findById(1L)).thenReturn(user);

        UserVO vo = userApplicationService.getUserById(1L);
        assertEquals(1L, vo.getId());
        assertEquals("test", vo.getUsername());
    }

    @Test
    @DisplayName("getUserById 不存在时抛出异常")
    void getUserById_notFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> userApplicationService.getUserById(99L));
    }

    @Test
    @DisplayName("createUser 密码过短时抛出异常")
    void createUser_weakPassword_shouldThrow() {
        com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO dto =
                new com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO();
        dto.setUsername("newuser");
        dto.setPassword("123");

        assertThrows(BizException.class, () -> userApplicationService.createUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser 合法输入应保存并返回ID")
    void createUser_valid_shouldSave() {
        com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO dto =
                new com.demetrius.vellastra.user.interfaces.dto.in.UserCreateDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("new@test.com");

        doAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return null;
        }).when(userRepository).save(any());

        Long id = userApplicationService.createUser(dto);

        assertEquals(10L, id);
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("updateUser 不存在时抛出异常")
    void updateUser_notFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () ->
                userApplicationService.updateUser(99L, new com.demetrius.vellastra.user.interfaces.dto.in.UserUpdateDTO()));
    }

    @Test
    @DisplayName("updateStatus 不存在时抛出异常")
    void updateStatus_notFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> userApplicationService.updateStatus(99L, 0));
    }

    @Test
    @DisplayName("deleteUser 不存在时抛出异常")
    void deleteUser_notFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> userApplicationService.deleteUser(99L));
    }

    @Test
    @DisplayName("resetPassword 不存在时抛出异常")
    void resetPassword_notFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> userApplicationService.resetPassword(99L));
    }

    @Test
    @DisplayName("changePassword 旧密码错误时抛出异常")
    void changePassword_wrongOldPassword_shouldThrow() {
        // 构造一个 BCrypt 密码：密码为 password123
        User user = User.builder().id(1L)
                .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode("password123")).build();
        when(userRepository.findById(1L)).thenReturn(user);

        com.demetrius.vellastra.user.interfaces.dto.in.PasswordUpdateDTO dto =
                new com.demetrius.vellastra.user.interfaces.dto.in.PasswordUpdateDTO();
        dto.setOldPassword("wrong");
        dto.setNewPassword("newpass123");

        assertThrows(BizException.class, () -> userApplicationService.changePassword(1L, dto));
        verify(userRepository, never()).save(any());
    }
}