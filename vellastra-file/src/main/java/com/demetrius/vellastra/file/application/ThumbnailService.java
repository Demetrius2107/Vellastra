package com.demetrius.vellastra.file.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ThumbnailService {

    @Value("${file.thumbnail.sizes:200,400,800}")
    private String thumbnailSizes;

    @Value("${file.thumbnail.quality:0.8}")
    private float quality;

    @Value("${file.upload-path:uploads}")
    private String uploadPath;

    /** 为图片生成多尺寸缩略图，返回缩略图路径列表 */
    public java.util.List<String> generateThumbnails(String objectName, byte[] imageData) {
        java.util.List<String> thumbnails = new java.util.ArrayList<>();
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageData));
            if (original == null) {
                log.warn("非图片格式，跳过缩略图生成: {}", objectName);
                return thumbnails;
            }

            for (String sizeStr : thumbnailSizes.split(",")) {
                int maxSize = Integer.parseInt(sizeStr.trim());
                String thumbPath = generateThumbnail(objectName, original, maxSize);
                if (thumbPath != null) thumbnails.add(thumbPath);
            }
            log.info("缩略图生成完成: {}, 尺寸: {}", objectName, thumbnails);
        } catch (Exception e) {
            log.warn("缩略图生成失败: {}", e.getMessage());
        }
        return thumbnails;
    }

    private String generateThumbnail(String objectName, BufferedImage original, int maxSize) throws Exception {
        int w = original.getWidth();
        int h = original.getHeight();
        if (w <= maxSize && h <= maxSize) return null;

        double scale = Math.min((double) maxSize / w, (double) maxSize / h);
        int tw = (int) (w * scale);
        int th = (int) (h * scale);

        BufferedImage thumb = new BufferedImage(tw, th, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumb.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, tw, th, null);
        g2d.dispose();

        String thumbName = objectName.replace(".", "_" + maxSize + ".");
        Path thumbFile = Paths.get(uploadPath, thumbName);
        Files.createDirectories(thumbFile.getParent());
        ImageIO.write(thumb, "jpg", thumbFile.toFile());
        return thumbName;
    }
}
