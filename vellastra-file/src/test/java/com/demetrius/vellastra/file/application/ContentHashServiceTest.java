package com.demetrius.vellastra.file.application;

import com.demetrius.vellastra.file.infrastructure.persistence.mapper.FileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContentHashServiceTest {

    @Mock
    private FileMapper fileMapper;

    private ContentHashService contentHashService;

    @BeforeEach
    void setUp() {
        contentHashService = new ContentHashService(fileMapper);
    }

    @Test
    @DisplayName("相同内容应计算出相同哈希")
    void computeHash_sameContent_shouldMatch() throws Exception {
        MockMultipartFile f1 = new MockMultipartFile("f", "a.txt", "text/plain", "hello world".getBytes());
        MockMultipartFile f2 = new MockMultipartFile("f", "b.txt", "text/plain", "hello world".getBytes());

        String hash1 = contentHashService.computeHash(f1);
        String hash2 = contentHashService.computeHash(f2);

        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }

    @Test
    @DisplayName("不同内容应计算出不同哈希")
    void computeHash_differentContent_shouldDiffer() throws Exception {
        MockMultipartFile f1 = new MockMultipartFile("f", "a.txt", "text/plain", "hello".getBytes());
        MockMultipartFile f2 = new MockMultipartFile("f", "b.txt", "text/plain", "world".getBytes());

        assertNotEquals(contentHashService.computeHash(f1), contentHashService.computeHash(f2));
    }

    @Test
    @DisplayName("字节数组哈希应为 64 位十六进制")
    void computeHash_byteArray_shouldReturn64Hex() {
        String hash = contentHashService.computeHash("test-data".getBytes());
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }
}
