package com.demetrius.vellastra.file.application;

import com.demetrius.vellastra.file.domain.file.entity.File;
import com.demetrius.vellastra.file.domain.file.repository.FileRepository;
import com.demetrius.vellastra.file.domain.file.valueobject.FileStatus;
import com.demetrius.vellastra.file.interfaces.dto.FileVO;
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
 * Unit tests for {@link FileApplicationService}
 */
@ExtendWith(MockitoExtension.class)
class FileApplicationServiceTest {

    @Mock
    private FileRepository fileRepository;

    private FileApplicationService fileApplicationService;

    @BeforeEach
    void setUp() {
        fileApplicationService = new FileApplicationService(fileRepository);
    }

    @Test
    @DisplayName("getFileById 存在时返回 FileVO")
    void getFileById_existing_shouldReturnVO() {
        File file = File.builder()
                .id(1L).userId(1L).fileName("test.png").filePath("/uploads/2026/07/test.png")
                .fileSize(1024L).fileType("image").mimeType("image/png")
                .storageType(FileStatus.LOCAL)
                .build();
        when(fileRepository.findById(1L)).thenReturn(file);

        FileVO vo = fileApplicationService.getFileById(1L);
        assertEquals("test.png", vo.getFileName());
        assertEquals("/uploads/2026/07/test.png", vo.getFilePath());
    }

    @Test
    @DisplayName("getFileById 不存在时返回 null")
    void getFileById_notFound_shouldReturnNull() {
        when(fileRepository.findById(99L)).thenReturn(null);
        assertNull(fileApplicationService.getFileById(99L));
    }
}