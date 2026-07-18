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
}