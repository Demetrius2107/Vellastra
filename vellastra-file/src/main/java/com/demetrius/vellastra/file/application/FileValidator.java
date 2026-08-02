package com.demetrius.vellastra.file.application;

import com.demetrius.vellastra.file.config.FileProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class FileValidator {

    private final FileProperties properties;

    public FileValidator(FileProperties properties) {
        this.properties = properties;
    }

    /** Magic Number -> 文件类型映射 */
    private static final Map<String, String> MAGIC_MAP = new HashMap<>();
    static {
        MAGIC_MAP.put("89504E47", "png");
        MAGIC_MAP.put("FFD8FF", "jpg");
        MAGIC_MAP.put("FFD8FFE0", "jpg");
        MAGIC_MAP.put("FFD8FFE1", "jpg");
        MAGIC_MAP.put("47494638", "gif");
        MAGIC_MAP.put("52494646", "webp");
        MAGIC_MAP.put("424D", "bmp");
        MAGIC_MAP.put("3C737667", "svg");
        MAGIC_MAP.put("25504446", "pdf");
        MAGIC_MAP.put("D0CF11E0", "doc");
        MAGIC_MAP.put("504B0304", "docx");
        MAGIC_MAP.put("504B0304", "xlsx");
        MAGIC_MAP.put("504B0304", "pptx");
        MAGIC_MAP.put("1F8B08", "gz");
        MAGIC_MAP.put("52617221", "rar");
        MAGIC_MAP.put("504B0304", "zip");
        MAGIC_MAP.put("0000001866747970", "mp4");
        MAGIC_MAP.put("664C6143", "flac");
        MAGIC_MAP.put("494433", "mp3");
    }

    private Set<String> allowedExtensions;

    @PostConstruct
    public void init() {
        allowedExtensions = new java.util.HashSet<>();
        allowedExtensions.addAll(properties.getAllowedImageTypes());
        allowedExtensions.addAll(properties.getAllowedDocTypes());
        allowedExtensions.addAll(properties.getAllowedVideoTypes());
        allowedExtensions.addAll(properties.getAllowedAudioTypes());
        log.info("文件校验器初始化完成，支持 {} 种格式", allowedExtensions.size());
    }

    /** 校验文件：类型 + 大小，抛出 IllegalArgumentException */
    public void validate(MultipartFile file, String category) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("文件不能为空");

        String extension = getExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension.toLowerCase()))
            throw new IllegalArgumentException("不支持的文件格式: " + extension);

        // Magic Number 校验
        String magicType = detectMagicType(file);
        if (magicType != null && !magicType.equals(extension.toLowerCase())) {
            log.warn("文件扩展名与 Magic Number 不匹配: ext={}, magic={}", extension, magicType);
            throw new IllegalArgumentException("文件格式校验失败，疑似篡改扩展名");
        }

        // 文件大小校验
        long maxSize = getMaxSize(category != null ? category : extension);
        if (file.getSize() > maxSize)
            throw new IllegalArgumentException(String.format("文件大小超过限制（最大 %d MB）", maxSize / 1024 / 1024));
    }

    /** 通过 Magic Number 检测真实文件类型 */
    public String detectMagicType(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] bytes = new byte[16];
            int read = is.read(bytes, 0, 16);
            if (read <= 0) return null;
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < read; i++) hex.append(String.format("%02X", bytes[i]));
            String hexStr = hex.toString();
            return MAGIC_MAP.entrySet().stream()
                    .filter(e -> hexStr.startsWith(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);
        } catch (IOException e) {
            log.warn("读取文件 Magic Number 失败: {}", e.getMessage());
            return null;
        }
    }

    /** 根据文件分类获取最大大小 */
    public long getMaxSize(String category) {
        if (category == null) return properties.getDefaultMaxSize();
        String cat = category.toLowerCase();
        if (properties.getAllowedImageTypes().contains(cat)) return properties.getMaxImageSize();
        if (properties.getAllowedDocTypes().contains(cat)) return properties.getMaxDocSize();
        if (properties.getAllowedVideoTypes().contains(cat)) return properties.getMaxVideoSize();
        if (properties.getAllowedAudioTypes().contains(cat)) return properties.getMaxAudioSize();
        return properties.getDefaultMaxSize();
    }

    public boolean isAllowedExtension(String ext) {
        return allowedExtensions.contains(ext.toLowerCase());
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
