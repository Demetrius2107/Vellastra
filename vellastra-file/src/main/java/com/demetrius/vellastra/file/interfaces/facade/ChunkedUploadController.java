package com.demetrius.vellastra.file.interfaces.facade;

import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.file.application.ChunkedUploadService;
import com.demetrius.vellastra.file.application.ContentHashService;
import com.demetrius.vellastra.file.application.FileApplicationService;
import com.demetrius.vellastra.file.application.FileValidator;
import com.demetrius.vellastra.file.application.ThumbnailService;
import com.demetrius.vellastra.file.domain.file.entity.File;
import com.demetrius.vellastra.file.domain.file.repository.FileRepository;
import com.demetrius.vellastra.file.interfaces.dto.FileVO;
import com.demetrius.vellastra.file.application.MinIOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/file")
public class ChunkedUploadController {

    private final ChunkedUploadService chunkedUploadService;
    private final ContentHashService contentHashService;
    private final FileRepository fileRepository;
    private final MinIOService minIOService;
    private final FileValidator fileValidator;
    private final ThumbnailService thumbnailService;

    public ChunkedUploadController(ChunkedUploadService chunkedUploadService,
                                   ContentHashService contentHashService,
                                   FileRepository fileRepository,
                                   MinIOService minIOService,
                                   FileValidator fileValidator,
                                   ThumbnailService thumbnailService) {
        this.chunkedUploadService = chunkedUploadService;
        this.contentHashService = contentHashService;
        this.fileRepository = fileRepository;
        this.minIOService = minIOService;
        this.fileValidator = fileValidator;
        this.thumbnailService = thumbnailService;
    }

    @PostMapping("/upload/init")
    public Result<Map<String, Object>> initUpload(@RequestParam String fileName,
                                                   @RequestParam long totalSize,
                                                   @RequestParam int totalChunks) {
        return Result.success(chunkedUploadService.initUpload(fileName, totalSize, totalChunks));
    }

    @PostMapping("/upload/chunk")
    public Result<Void> uploadChunk(@RequestParam String uploadId,
                                     @RequestParam int chunkIndex,
                                     @RequestParam("file") MultipartFile chunk) {
        try {
            chunkedUploadService.uploadChunk(uploadId, chunkIndex, chunk.getBytes());
            return Result.success();
        } catch (Exception e) {
            throw new RuntimeException("分块上传失败", e);
        }
    }

    @PostMapping("/upload/complete")
    public Result<FileVO> completeUpload(@RequestParam String uploadId,
                                          @RequestParam String fileName,
                                          @RequestHeader("X-User-Id") Long userId,
                                          @RequestParam(defaultValue = "image") String category) {
        // 合并分块
        ChunkedUploadService.MergeResult merged = chunkedUploadService.completeUpload(uploadId, fileName);

        // 去重检查
        Optional<com.demetrius.vellastra.file.infrastructure.persistence.po.FilePO> existing = contentHashService.findByHash(merged.hash());
        if (existing.isPresent()) {
            log.info("文件已存在（去重命中）: hash={}, originalId={}", merged.hash(), existing.get().getId());
            FileVO vo = new FileVO();
            vo.setId(existing.get().getId());
            vo.setFileName(merged.fileName());
            vo.setFileSize(existing.get().getFileSize());
            vo.setFileType(existing.get().getFileType());
            return Result.success(vo);
        }

        // 上传到 MinIO
        String ext = merged.fileName().contains(".") ? merged.fileName().substring(merged.fileName().lastIndexOf(".") + 1) : "";
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String objectName = dateDir + "/" + uuid + "." + ext;

        try {
            String url = minIOService.upload(objectName, new java.io.ByteArrayInputStream(merged.data()),
                    merged.data().length, "application/octet-stream");

            // 生成缩略图
            if ("image".equals(category)) {
                thumbnailService.generateThumbnails(objectName, merged.data());
            }

            // 保存记录
            var po = new com.demetrius.vellastra.file.infrastructure.persistence.po.FilePO();
            po.setUserId(userId);
            po.setFileName(merged.fileName());
            po.setFilePath(objectName);
            po.setFileSize((long) merged.data().length);
            po.setFileType(category);
            po.setContentHash(merged.hash());
            po.setCreateTime(LocalDateTime.now());
            po.setUpdateTime(LocalDateTime.now());
            fileRepository.save(new File());

            FileVO vo = new FileVO();
            vo.setFileName(merged.fileName());
            vo.setFileSize((long) merged.data().length);
            vo.setUrl(url);
            vo.setCreateTime(LocalDateTime.now());
            return Result.success(vo);
        } catch (Exception e) {
            log.error("分块上传完成处理失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @PostMapping("/upload/cancel")
    public Result<Void> cancelUpload(@RequestParam String uploadId) {
        chunkedUploadService.cancelUpload(uploadId);
        return Result.success();
    }
}
