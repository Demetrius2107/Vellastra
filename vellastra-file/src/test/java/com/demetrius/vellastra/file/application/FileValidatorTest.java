package com.demetrius.vellastra.file.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorTest {

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        fileValidator = new FileValidator(new com.demetrius.vellastra.file.config.FileProperties());
        fileValidator.init();
    }

    @Test
    @DisplayName("允许的扩展名应返回 true")
    void isAllowedExtension_shouldAcceptCommonTypes() {
        assertTrue(fileValidator.isAllowedExtension("jpg"));
        assertTrue(fileValidator.isAllowedExtension("PNG"));
        assertTrue(fileValidator.isAllowedExtension("pdf"));
        assertTrue(fileValidator.isAllowedExtension("mp4"));
    }

    @Test
    @DisplayName("不允许的扩展名应返回 false")
    void isAllowedExtension_shouldRejectUnsafeTypes() {
        assertFalse(fileValidator.isAllowedExtension("exe"));
        assertFalse(fileValidator.isAllowedExtension("sh"));
        assertFalse(fileValidator.isAllowedExtension("php"));
    }

    @Test
    @DisplayName("图片扩展名最大大小应为 5MB")
    void getMaxSize_shouldReturnImageLimit() {
        assertEquals(5 * 1024 * 1024L, fileValidator.getMaxSize("jpg"));
        assertEquals(5 * 1024 * 1024L, fileValidator.getMaxSize("png"));
    }

    @Test
    @DisplayName("视频扩展名最大大小应为 500MB")
    void getMaxSize_video_shouldReturnLimit() {
        assertEquals(500 * 1024 * 1024L, fileValidator.getMaxSize("mp4"));
    }

    @Test
    @DisplayName("未知扩展名应返回默认大小")
    void getMaxSize_unknownCategory_shouldReturnDefault() {
        assertEquals(10 * 1024 * 1024L, fileValidator.getMaxSize("unknown"));
    }
}
