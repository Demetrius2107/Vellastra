package com.demetrius.vellastra.file.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: FileVO</p>
 * <p>Description: 文件视图对象</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
public class FileVO {
    /** 文件ID */
    private Long id;
    /** 原始文件名 */
    private String fileName;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 文件类型: image/video/document */
    private String fileType;
    /** 文件访问URL */
    private String url;
    /** 创建时间 */
    private LocalDateTime createTime;
}
