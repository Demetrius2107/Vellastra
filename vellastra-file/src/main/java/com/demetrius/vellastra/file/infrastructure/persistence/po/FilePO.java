package com.demetrius.vellastra.file.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <h3>文件持久化对象</h3>
 *
 * <p>与 sys_media 表 1:1 对应，基础设施层持久映射。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@TableName("sys_media")
public class FilePO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
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

    /** 存储方式: 1本地存储 2MinIO 3OSS */
    private Integer storageType;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}