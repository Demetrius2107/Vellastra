package com.demetrius.vellastra.file.domain.file.valueobject;

/**
 * <h3>文件存储类型枚举</h3>
 *
 * <p>对应 sys_media 表的 storage_type 字段。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
public enum FileStatus {

    /** 本地存储 */
    LOCAL(1),
    /** MinIO 对象存储 */
    MINIO(2),
    /** 阿里云 OSS */
    OSS(3);

    private final int code;

    FileStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FileStatus fromCode(int code) {
        for (FileStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return LOCAL;
    }
}