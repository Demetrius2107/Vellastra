package com.demetrius.vellastra.file.infrastructure.persistence;

import com.demetrius.vellastra.file.domain.file.entity.File;
import com.demetrius.vellastra.file.domain.file.repository.FileRepository;
import com.demetrius.vellastra.file.infrastructure.persistence.converter.FileConverter;
import com.demetrius.vellastra.file.infrastructure.persistence.mapper.FileMapper;
import com.demetrius.vellastra.file.infrastructure.persistence.po.FilePO;
import org.springframework.stereotype.Repository;

/**
 * <p>Title: FileRepositoryImpl</p>
 * <p>Description: 文件仓储实现（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Repository
public class FileRepositoryImpl implements FileRepository {

    private final FileMapper fileMapper;
    private final FileConverter fileConverter;

    public FileRepositoryImpl(FileMapper fileMapper, FileConverter fileConverter) {
        this.fileMapper = fileMapper;
        this.fileConverter = fileConverter;
    }

    @Override
    public void save(File file) {
        FilePO po = fileConverter.toPO(file);
        if (po.getId() == null) {
            fileMapper.insert(po);
            file.setId(po.getId());
        } else {
            fileMapper.updateById(po);
        }
    }

    @Override
    public File findById(Long id) {
        FilePO po = fileMapper.selectById(id);
        return po != null ? fileConverter.toDomain(po) : null;
    }

    @Override
    public void delete(Long id) {
        fileMapper.deleteById(id);
    }
}
