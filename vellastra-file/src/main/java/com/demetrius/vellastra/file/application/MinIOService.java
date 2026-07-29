package com.demetrius.vellastra.file.application;

import com.demetrius.vellastra.file.config.MinIOProperties;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: MinIOService</p>
 * <p>Description: MinIO 对象存储服务，提供文件上传、下载、删除等操作</p>
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
@Component
public class MinIOService {

    private final MinIOProperties properties;
    private MinioClient client;

    public MinIOService(MinIOProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        client = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        // 确保存储桶存在
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucket()).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
                log.info("MinIO 存储桶创建成功: {}", properties.getBucket());
            }
        } catch (Exception e) {
            log.warn("MinIO 初始化失败（可能未启动）: {}", e.getMessage());
        }
        log.info("MinIO 客户端初始化完成: endpoint={}", properties.getEndpoint());
    }

    /**
     * 上传文件
     *
     * @param objectName 对象名（如 "2026/07/29/uuid.jpg"）
     * @param stream     文件流
     * @param size       文件大小
     * @param contentType MIME 类型
     * @return 文件访问 URL
     */
    public String upload(String objectName, InputStream stream, long size, String contentType) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .stream(stream, size, -1)
                    .contentType(contentType)
                    .build());
            // 返回公开访问 URL
            if (properties.getPublicUrl() != null) {
                return properties.getPublicUrl() + "/" + properties.getBucket() + "/" + objectName;
            }
            return properties.getEndpoint() + "/" + properties.getBucket() + "/" + objectName;
        } catch (Exception e) {
            log.error("MinIO 上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名
     */
    public void delete(String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("MinIO 删除失败: {}", e.getMessage());
        }
    }
}