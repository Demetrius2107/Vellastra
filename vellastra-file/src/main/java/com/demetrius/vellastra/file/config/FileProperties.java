package com.demetrius.vellastra.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    private String uploadPath = "uploads";
    private String tempDir = "temp";
    private List<String> allowedImageTypes = List.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");
    private List<String> allowedDocTypes = List.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md");
    private List<String> allowedVideoTypes = List.of("mp4", "avi", "mov", "wmv", "flv");
    private List<String> allowedAudioTypes = List.of("mp3", "wav", "ogg", "aac", "flac");

    private long maxImageSize = 5 * 1024 * 1024L;       // 5MB
    private long maxDocSize = 50 * 1024 * 1024L;         // 50MB
    private long maxVideoSize = 500 * 1024 * 1024L;      // 500MB
    private long maxAudioSize = 100 * 1024 * 1024L;      // 100MB
    private long defaultMaxSize = 10 * 1024 * 1024L;     // 10MB

    private int chunkExpireHours = 24;                    // 分块上传临时文件保留时间
}
