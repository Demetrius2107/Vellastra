package com.demetrius.vellastra.mail.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MailTemplateRendererTest {

    private MailTemplateRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new MailTemplateRenderer();
    }

    @Test
    @DisplayName("render 应替换 {{var}} 变量")
    void render_shouldReplaceVariables() {
        String template = "你好 {{username}}，欢迎加入 {{site}}！";
        String result = renderer.render(template, Map.of("username", "张三", "site", "Vellastra"));

        assertEquals("你好 张三，欢迎加入 Vellastra！", result);
    }

    @Test
    @DisplayName("render 缺失的变量应替换为空")
    void render_missingVariable_shouldReplaceWithEmpty() {
        // 传入非空 Map 但不含 username 变量
        String result = renderer.render("你好 {{username}}", Map.of("name", "张三"));

        assertEquals("你好 ", result);
    }

    @Test
    @DisplayName("render 空变量 Map 应返回原模板")
    void render_emptyVariables_shouldReturnTemplate() {
        String result = renderer.render("你好 {{username}}", Map.of());

        assertEquals("你好 {{username}}", result);
    }

    @Test
    @DisplayName("render null 模板应返回 null")
    void render_nullTemplate_shouldReturnNull() {
        assertNull(renderer.render(null, Map.of()));
    }

    @Test
    @DisplayName("buildConfirmLink 应生成确认链接")
    void buildConfirmLink_shouldBuildUrl() {
        String link = renderer.buildConfirmLink("http://localhost:8080", "token123");
        assertEquals("http://localhost:8080/mail/confirm?token=token123", link);
    }

    @Test
    @DisplayName("buildUnsubscribeLink 应生成退订链接")
    void buildUnsubscribeLink_shouldBuildUrl() {
        String link = renderer.buildUnsubscribeLink("http://localhost:8080", "token456");
        assertEquals("http://localhost:8080/mail/unsubscribe?token=token456", link);
    }
}
