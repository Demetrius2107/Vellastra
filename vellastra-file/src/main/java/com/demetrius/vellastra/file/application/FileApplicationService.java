package com.demetrius.vellastra.file.application;

import com.demetrius.vellastra.file.domain.file.entity.File;
import com.demetrius.vellastra.file.domain.file.repository.FileRepository;
import com.demetrius.vellastra.file.interfaces.dto.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * <p>Title: FileApplicationService</p>
 * <p>Description: 文件应用服务，负责文件上传、访问等业务逻辑</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Service
public class FileApplicationService {

    private final FileRepository fileRepository;
    private final MinIOService minIOService;
    private final FileValidator fileValidator;

    public FileApplicationService(FileRepository fileRepository, MinIOService minIOService,
                                  FileValidator fileValidator) {
        this.fileRepository = fileRepository;
        this.minIOService = minIOService;
        this.fileValidator = fileValidator;
    }

    @Transactional
    public FileVO upload(MultipartFile file, Long userId, String category) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("文件不能为空");

        // 使用 FileValidator 统一校验（Magic Number + 文件大小）
        fileValidator.validate(file, category);

        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName);
        long size = file.getSize();
        boolean isImage = fileValidator.isAllowedExtension(ext) && category != null && category.equals("image");

        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String objectName = dateDir + "/" + uuid + "." + ext;

        try {
            String url = minIOService.upload(objectName, file.getInputStream(), size, file.getContentType());
            File fileEntity = File.builder()
                    .userId(userId)
                    .fileName(originalName)
                    .filePath(objectName)
                    .fileSize(size)
                    .fileType(isImage ? "image" : "document")
                    .mimeType(file.getContentType())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            fileRepository.save(fileEntity);
            log.info("文件上传成功: id={}, name={}, size={}", fileEntity.getId(), originalName, size);

            FileVO vo = toVO(fileEntity);
            vo.setUrl(url);
            return vo;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isImageType(String ext) {
        return fileValidator.isAllowedExtension(ext) && 
               java.util.Set.of("jpg","jpeg","png","gif","webp","bmp","svg").contains(ext);
    }

    private boolean isAllowedExtension(String ext) {
        return fileValidator.isAllowedExtension(ext);
    }

    private FileVO toVO(File file) {
        FileVO vo = new FileVO();
        vo.setId(file.getId());
        vo.setFileName(file.getFileName());
        vo.setFileSize(file.getFileSize());
        vo.setFileType(file.getFileType());
        vo.setCreateTime(file.getCreateTime());
        return vo;
    }
}
