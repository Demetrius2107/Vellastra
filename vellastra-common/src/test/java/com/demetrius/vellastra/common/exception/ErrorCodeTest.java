package com.demetrius.vellastra.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ErrorCode}
 */
class ErrorCodeTest {

    @Test
    @DisplayName("toException() 应返回正确 code 和 message 的 BizException")
    void toException_shouldReturnCorrectBizException() {
        BizException ex = ErrorCode.ARTICLE_NOT_FOUND.toException();
        assertEquals(3001, ex.getCode());
        assertEquals("文章不存在", ex.getMessage());
    }

    @Test
    @DisplayName("SUCCESS 的 code 应为 200")
    void success_shouldHaveCode200() {
        assertEquals(200, ErrorCode.SUCCESS.getCode());
    }

    @Test
    @DisplayName("SYSTEM_ERROR 的 code 应为 500")
    void systemError_shouldHaveCode500() {
        assertEquals(500, ErrorCode.SYSTEM_ERROR.getCode());
    }
}