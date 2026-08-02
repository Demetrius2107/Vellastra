package com.demetrius.vellastra.file.application;

import com.demetrius.vellastra.file.infrastructure.persistence.mapper.FileMapper;
import com.demetrius.vellastra.file.infrastructure.persistence.po.FilePO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Optional;

@Slf4j
@Service
public class ContentHashService {

    private final FileMapper fileMapper;

    public ContentHashService(FileMapper fileMapper) { this.fileMapper = fileMapper; }

    public String computeHash(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) md.update(buffer, 0, read);
            StringBuilder hex = new StringBuilder();
            for (byte b : md.digest()) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算文件哈希失败", e);
        }
    }

    public String computeHash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算文件哈希失败", e);
        }
    }

    public Optional<FilePO> findByHash(String hash) {
        return Optional.ofNullable(fileMapper.selectOne(
                new LambdaQueryWrapper<FilePO>().eq(FilePO::getContentHash, hash)));
    }
}
