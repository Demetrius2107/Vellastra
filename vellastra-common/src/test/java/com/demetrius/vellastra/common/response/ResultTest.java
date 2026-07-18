package com.demetrius.vellastra.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Result}
 */
class ResultTest {

    @Test
    @DisplayName("success() 应返回 code=200, message=success, data=null")
    void success_shouldReturnDefault() {
        Result<Void> result = Result.success();
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("success(data) 应返回 code=200 并携带数据")
    void successWithData_shouldReturnData() {
        Result<String> result = Result.success("hello");
        assertEquals(200, result.getCode());
        assertEquals("hello", result.getData());
    }

    @Test
    @DisplayName("fail(code, message) 应返回自定义错误码和消息")
    void fail_shouldReturnCustomError() {
        Result<Void> result = Result.fail(404, "资源不存在");
        assertEquals(404, result.getCode());
        assertEquals("资源不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("fail(message) 应返回默认 code=500")
    void failWithMessage_shouldReturn500() {
        Result<Void> result = Result.fail("服务器错误");
        assertEquals(500, result.getCode());
        assertEquals("服务器错误", result.getMessage());
    }
}