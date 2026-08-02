package com.demetrius.vellastra.gateway.filter;

import com.demetrius.vellastra.common.service.TokenBlackListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthGlobalFilterTest {

    @Mock
    private TokenBlackListService tokenBlackListService;

    @Mock
    private GatewayFilterChain chain;

    private AuthGlobalFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthGlobalFilter(tokenBlackListService);
        ReflectionTestUtils.setField(filter, "jwtSecret",
                "test-secret-key-must-be-at-least-256-bits-long-for-hmac-sha");
        ReflectionTestUtils.setField(filter, "whiteListConfig",
                "/auth/login,/auth/register,/actuator/**,/doc.html,/v3/api-docs,/swagger-ui/**,/webjars/**");
    }

    @Test
    @DisplayName("白名单路径应直接放行（无需 Token）")
    void filter_whiteListPath_shouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    @DisplayName("非白名单路径无 Token 应返回 401")
    void filter_noToken_shouldReturnUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/article/list").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("非白名单路径无效 Token 应返回 401")
    void filter_invalidToken_shouldReturnUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/article/list")
                .header("Authorization", "Bearer invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
        verify(chain, never()).filter(any());
    }
}
