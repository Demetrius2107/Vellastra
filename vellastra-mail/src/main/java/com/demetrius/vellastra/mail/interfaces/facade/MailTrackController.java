package com.demetrius.vellastra.mail.interfaces.facade;

import com.demetrius.vellastra.mail.infrastructure.mapper.EmailSendLogMapper;
import com.demetrius.vellastra.mail.infrastructure.po.EmailSendLogPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/mail/track")
public class MailTrackController {

    private final EmailSendLogMapper sendLogMapper;

    public MailTrackController(EmailSendLogMapper sendLogMapper) {
        this.sendLogMapper = sendLogMapper;
    }

    /**
     * 打开追踪：返回 1x1 透明像素图片，同时记录打开时间
     * 邮件 HTML 中嵌入 <img src="/mail/track/open/{logId}">
     */
    @GetMapping(value = "/open/{logId}", produces = "image/gif")
    public ResponseEntity<byte[]> trackOpen(@PathVariable Long logId) {
        EmailSendLogPO po = sendLogMapper.selectById(logId);
        if (po != null && po.getOpenedAt() == null) {
            po.setOpenedAt(LocalDateTime.now());
            po.setUpdateTime(LocalDateTime.now());
            sendLogMapper.updateById(po);
            log.info("邮件打开追踪: id={}, to={}", logId, po.getTo());
        }
        // 1x1 透明 GIF
        byte[] pixel = Base64.getDecoder().decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7");
        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .body(pixel);
    }

    /**
     * 点击追踪：记录点击时间后 302 跳转到目标 URL
     * 邮件 HTML 中链接使用 /mail/track/click/{logId}?url=目标地址
     */
    @GetMapping("/click/{logId}")
    public ResponseEntity<Void> trackClick(@PathVariable Long logId, @RequestParam String url) {
        EmailSendLogPO po = sendLogMapper.selectById(logId);
        if (po != null && po.getClickedAt() == null) {
            po.setClickedAt(LocalDateTime.now());
            po.setUpdateTime(LocalDateTime.now());
            sendLogMapper.updateById(po);
            log.info("邮件点击追踪: id={}, to={}, url={}", logId, po.getTo(), url);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }
}
