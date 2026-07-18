package com.demetrius.vellastra.file.domain.file.entity;

import com.demetrius.vellastra.file.domain.file.valueobject.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <h3>文件领域实体</h3>
 *
 * <p>对应 sys_media 表，管理上传的媒体文件（图片/视频/文档）。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

    /** 主键ID */
    private Long id;

    /** 上传用户ID */
    private Long userId;

    /** 原始文件名 */
    private String fileName;

    /** 文件存储路径 */
    private String filePath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型: image/video/document */
    private String fileType;

    /** MIME类型 */
    private String mimeType;

    /** 存储方式 */
    private FileStatus storageType;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}