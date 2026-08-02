package com.demetrius.vellastra.file.application;

import com.demetrius.vellastra.file.config.FileProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChunkedUploadService {

    private final FileProperties properties;
    private final ContentHashService contentHashService;

    /** 上传会话：uploadId -> {chunkIndex -> filePath} */
    private final ConcurrentHashMap<String, ChunkSession> sessions = new ConcurrentHashMap<>();

    public ChunkedUploadService(FileProperties properties, ContentHashService contentHashService) {
        this.properties = properties;
        this.contentHashService = contentHashService;
    }

    @PostConstruct
    public void init() {
        try {
            Path tempDir = Paths.get(properties.getUploadPath(), properties.getTempDir());
            Files.createDirectories(tempDir);
        } catch (IOException e) {
            log.warn("创建分块上传临时目录失败: {}", e.getMessage());
        }
    }

    /** 初始化分块上传 */
    public Map<String, Object> initUpload(String fileName, long totalSize, int totalChunks) {
        String uploadId = UUID.randomUUID().toString().replace("-", "");
        sessions.put(uploadId, new ChunkSession(uploadId, fileName, totalSize, totalChunks));
        log.info("初始化分块上传: uploadId={}, fileName={}, totalChunks={}", uploadId, fileName, totalChunks);
        return Map.of("uploadId", uploadId, "chunkSize", getChunkSize(totalSize), "totalChunks", totalChunks);
    }

    /** 上传分块 */
    public void uploadChunk(String uploadId, int chunkIndex, byte[] data) {
        ChunkSession session = sessions.get(uploadId);
        if (session == null) throw new RuntimeException("上传会话不存在: " + uploadId);

        try {
            Path chunkDir = getChunkDir(uploadId);
            Files.createDirectories(chunkDir);
            Path chunkFile = chunkDir.resolve(String.valueOf(chunkIndex));
            Files.write(chunkFile, data);
            session.markChunkUploaded(chunkIndex);
        } catch (IOException e) {
            throw new RuntimeException("分块写入失败", e);
        }
    }

    /** 合并分块并去重 */
    public MergeResult completeUpload(String uploadId, String fileName) {
        ChunkSession session = sessions.get(uploadId);
        if (session == null) throw new RuntimeException("上传会话不存在: " + uploadId);

        Path chunkDir = getChunkDir(uploadId);
        Path mergedFile = Paths.get(properties.getUploadPath(), properties.getTempDir(), uploadId + "_" + fileName);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(mergedFile.toFile()))) {
            for (int i = 0; i < session.totalChunks; i++) {
                Path chunkPath = chunkDir.resolve(String.valueOf(i));
                if (Files.exists(chunkPath)) {
                    Files.copy(chunkPath, out);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("合并分块失败", e);
        }

        // 计算合并后文件的哈希（去重）
        byte[] fileBytes;
        try {
            fileBytes = Files.readAllBytes(mergedFile);
        } catch (IOException e) {
            throw new RuntimeException("读取合并文件失败", e);
        }
        String hash = contentHashService.computeHash(fileBytes);

        // 清理临时文件
        cleanup(uploadId);
        try { Files.deleteIfExists(mergedFile); } catch (IOException ignored) {}

        log.info("分块上传完成: uploadId={}, size={}, hash={}", uploadId, fileBytes.length, hash);
        return new MergeResult(uploadId, fileBytes, hash, fileName);
    }

    /** 取消上传并清理 */
    public void cancelUpload(String uploadId) {
        cleanup(uploadId);
        log.info("取消分块上传: uploadId={}", uploadId);
    }

    public record MergeResult(String uploadId, byte[] data, String hash, String fileName) {}

    // ===================== 内部 =====================

    private void cleanup(String uploadId) {
        sessions.remove(uploadId);
        try {
            Path chunkDir = getChunkDir(uploadId);
            if (Files.exists(chunkDir)) {
                try (var files = Files.walk(chunkDir)) {
                    files.sorted(Comparator.reverseOrder()).forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
                }
            }
        } catch (IOException ignored) {}
    }

    private Path getChunkDir(String uploadId) {
        return Paths.get(properties.getUploadPath(), properties.getTempDir(), "chunks", uploadId);
    }

    private long getChunkSize(long totalSize) {
        // 目标分块大小 5MB，最少 1MB，最多 50MB
        long target = 5 * 1024 * 1024L;
        if (totalSize < 10 * 1024 * 1024L) return 1024 * 1024L;
        if (totalSize > 1024 * 1024 * 1024L) return 50 * 1024 * 1024L;
        return target;
    }

    private static class ChunkSession {
        final String uploadId;
        final String fileName;
        final long totalSize;
        final int totalChunks;
        final BitSet uploadedChunks = new BitSet();

        ChunkSession(String uploadId, String fileName, long totalSize, int totalChunks) {
            this.uploadId = uploadId;
            this.fileName = fileName;
            this.totalSize = totalSize;
            this.totalChunks = totalChunks;
        }

        void markChunkUploaded(int index) { uploadedChunks.set(index); }
    }
}
