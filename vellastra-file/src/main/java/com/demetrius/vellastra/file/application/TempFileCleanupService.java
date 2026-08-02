package com.demetrius.vellastra.file.application;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Service
public class TempFileCleanupService {

    @Value("${file.upload-path:uploads}")
    private String uploadPath;

    @Value("${file.chunk-expire-hours:24}")
    private int chunkExpireHours;

    @Value("${file.temp-dir:temp}")
    private String tempDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath, tempDir));
        } catch (IOException e) {
            log.warn("创建临时目录失败: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "${file.cleanup-cron:0 0 4 * * ?}")
    public void cleanExpiredChunks() {
        Path tempPath = Paths.get(uploadPath, tempDir);
        if (!Files.exists(tempPath)) return;

        long deadline = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(chunkExpireHours);
        int count = 0;
        try (Stream<Path> files = Files.walk(tempPath)) {
            for (Path file : files.toList()) {
                if (Files.isRegularFile(file)) {
                    BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                    if (attrs.lastModifiedTime().toMillis() < deadline) {
                        Files.delete(file);
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            log.warn("清理临时文件失败: {}", e.getMessage());
        }
        if (count > 0) log.info("临时文件清理完成: 清理 {} 个过期分块文件", count);
    }
}
