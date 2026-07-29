package com.demetrius.vellastra.file.domain.file.repository;

import com.demetrius.vellastra.file.domain.file.entity.File;

/**
 * <p>Title: FileRepository</p>
 * <p>Description: 文件仓储接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface FileRepository {

    /** 保存文件记录 */
    void save(File file);

    /** 根据ID查找文件 */
    File findById(Long id);

    /** 删除文件记录 */
    void delete(Long id);
}
