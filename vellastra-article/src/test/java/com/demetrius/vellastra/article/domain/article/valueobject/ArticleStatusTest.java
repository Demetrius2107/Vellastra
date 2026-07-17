package com.demetrius.vellastra.article.domain.article.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章状态枚举单元测试
 */
@DisplayName("ArticleStatus 枚举")
class ArticleStatusTest {

    // ======================== 枚举常量 ========================

    @Test
    @DisplayName("应包含所有预期状态值")
    void shouldContainAllExpectedStatuses() {
        assertEquals(4, ArticleStatus.values().length);
        assertNotNull(ArticleStatus.valueOf("DRAFT"));
        assertNotNull(ArticleStatus.valueOf("REVIEWING"));
        assertNotNull(ArticleStatus.valueOf("PUBLISHED"));
        assertNotNull(ArticleStatus.valueOf("OFFLINE"));
    }

    @ParameterizedTest
    @MethodSource("statusDataProvider")
    @DisplayName("每个枚举的 code 和 desc 应正确")
    void statusShouldHaveCorrectCodeAndDesc(ArticleStatus status, int expectedCode, String expectedDesc) {
        assertEquals(expectedCode, status.getCode());
        assertEquals(expectedDesc, status.getDesc());
    }

    static Stream<Arguments> statusDataProvider() {
        return Stream.of(
                Arguments.of(ArticleStatus.DRAFT, 0, "草稿"),
                Arguments.of(ArticleStatus.REVIEWING, 1, "待审核"),
                Arguments.of(ArticleStatus.PUBLISHED, 2, "已发布"),
                Arguments.of(ArticleStatus.OFFLINE, 3, "下架")
        );
    }

    // ======================== of() 方法 ========================

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("of() 应按 code 正确解析枚举")
    void ofShouldResolveByCode(int code) {
        ArticleStatus status = ArticleStatus.of(code);
        assertNotNull(status);
        assertEquals(code, status.getCode());
    }

    @Test
    @DisplayName("of() 对未知 code 应返回 DRAFT 作为默认值")
    void ofShouldReturnDraftForUnknownCode() {
        assertEquals(ArticleStatus.DRAFT, ArticleStatus.of(-1));
        assertEquals(ArticleStatus.DRAFT, ArticleStatus.of(99));
        assertEquals(ArticleStatus.DRAFT, ArticleStatus.of(Integer.MIN_VALUE));
    }

    // ======================== 边界值 ========================

    @Test
    @DisplayName("code 值应覆盖 0~3 的连续范围")
    void codesShouldBeContinuous() {
        for (int i = 0; i < ArticleStatus.values().length; i++) {
            assertEquals(i, ArticleStatus.values()[i].getCode());
        }
    }
}