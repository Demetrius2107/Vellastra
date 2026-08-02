package com.demetrius.vellastra.mail.interfaces.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.mail.application.SendEngineService;
import com.demetrius.vellastra.mail.infrastructure.po.EmailSendLogPO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mail/send")
public class SendController {

    private final SendEngineService sendEngineService;

    public SendController(SendEngineService sendEngineService) {
        this.sendEngineService = sendEngineService;
    }

    @PostMapping("/single")
    public Result<Long> sendSingle(@RequestParam String to,
                                   @RequestParam String templateCode,
                                   @RequestBody(required = false) Map<String, Object> variables) {
        return Result.success(sendEngineService.sendWithTemplate(to, templateCode, variables));
    }

    @PostMapping("/batch")
    public Result<String> sendBatch(@RequestParam String templateCode,
                                    @RequestBody List<String> emails) {
        return Result.success(sendEngineService.sendBatch(emails, templateCode, Map.of()));
    }

    @PostMapping("/{id}/retry")
    public Result<Void> retry(@PathVariable Long id) {
        sendEngineService.retry(id);
        return Result.success();
    }

    @PostMapping("/{id}/bounce")
    public Result<Void> markBounced(@PathVariable Long id, @RequestParam String reason) {
        sendEngineService.markBounced(id, reason);
        return Result.success();
    }

    @GetMapping("/logs")
    public Result<IPage<EmailSendLogPO>> logs(@RequestParam(defaultValue = "1") int current,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(required = false) String batchNo) {
        return Result.success(sendEngineService.listLogs(current, size, status, batchNo));
    }
}
