package com.demetrius.vellastra.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>Title: MinIOProperties</p>
 * <p>Description: MinIO 对象存储配置属性</p>
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
@Component
@ConfigurationProperties(prefix = "minio")
public class MinIOProperties {
    /** 服务地址 */
    private String endpoint = "http://localhost:9000";
    /** Access Key */
    private String accessKey = "minioadmin";
    /** Secret Key */
    private String secretKey = "minioadmin";
    /** 默认存储桶 */
    private String bucket = "vellastra";
    /** 文件访问前缀 */
    private String publicUrl;
}
