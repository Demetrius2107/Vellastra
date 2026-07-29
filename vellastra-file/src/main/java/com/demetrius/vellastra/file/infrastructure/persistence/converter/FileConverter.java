package com.demetrius.vellastra.file.infrastructure.persistence.converter;

import com.demetrius.vellastra.file.domain.file.entity.File;
import com.demetrius.vellastra.file.infrastructure.persistence.po.FilePO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: FileConverter</p>
 * <p>Description: 文件转换器（PO ↔ Domain）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Component
public class FileConverter {

    public File toDomain(FilePO po) {
        if (po == null) return null;
        return File.builder()
                .id(po.getId())
                .userId(po.getUserId())
                .fileName(po.getFileName())
                .filePath(po.getFilePath())
                .fileSize(po.getFileSize())
                .fileType(po.getFileType())
                .mimeType(po.getMimeType())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    public FilePO toPO(File domain) {
        if (domain == null) return null;
        FilePO po = new FilePO();
        po.setId(domain.getId());
        po.setUserId(domain.getUserId());
        po.setFileName(domain.getFileName());
        po.setFilePath(domain.getFilePath());
        po.setFileSize(domain.getFileSize());
        po.setFileType(domain.getFileType());
        po.setMimeType(domain.getMimeType());
        po.setStorageType(1);
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
