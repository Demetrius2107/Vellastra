package com.demetrius.vellastra.file.interfaces.facade;

import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.file.application.FileApplicationService;
import com.demetrius.vellastra.file.interfaces.dto.FileVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>Title: FileController</p>
 * <p>Description: 文件上传控制器，提供文件上传接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileApplicationService fileApplicationService;

    public FileController(FileApplicationService fileApplicationService) {
        this.fileApplicationService = fileApplicationService;
    }

    /**
     * 上传文件
     *
     * @param file   上传的文件
     * @param userId 上传用户ID（请求头）
     * @return 文件视图对象（含访问URL）
     */
    @PostMapping("/upload")
    public Result<FileVO> upload(@RequestParam("file") MultipartFile file,
                                 @RequestHeader("X-User-Id") Long userId,
                                 @RequestParam(defaultValue = "image") String category) {
        return Result.success(fileApplicationService.upload(file, userId, category));
    }
}
