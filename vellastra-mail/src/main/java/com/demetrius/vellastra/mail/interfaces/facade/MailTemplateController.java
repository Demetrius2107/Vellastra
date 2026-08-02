package com.demetrius.vellastra.mail.interfaces.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.mail.application.MailTemplateService;
import com.demetrius.vellastra.mail.infrastructure.po.MailTemplatePO;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mail/templates")
public class MailTemplateController {

    private final MailTemplateService templateService;

    public MailTemplateController(MailTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public Result<IPage<MailTemplatePO>> list(@RequestParam(defaultValue = "1") int current,
                                              @RequestParam(defaultValue = "10") int size) {
        return Result.success(templateService.list(current, size));
    }

    @GetMapping("/{id}")
    public Result<MailTemplatePO> getById(@PathVariable Long id) {
        return Result.success(templateService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@RequestBody MailTemplatePO request) {
        return Result.success(templateService.create(request.getName(), request.getCode(),
                request.getSubject(), request.getContent(), request.getCreatedBy()));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MailTemplatePO request) {
        templateService.update(id, request.getName(), request.getSubject(),
                request.getContent(), request.getStatus());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/preview")
    public Result<Map<String, String>> preview(@PathVariable Long id, @RequestBody Map<String, Object> variables) {
        return Result.success(templateService.preview(id, variables));
    }
}
