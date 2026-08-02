package com.demetrius.vellastra.mail.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.mail.infrastructure.mapper.MailTemplateMapper;
import com.demetrius.vellastra.mail.infrastructure.po.MailTemplatePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class MailTemplateService {

    private final MailTemplateMapper templateMapper;
    private final MailTemplateRenderer renderer;

    public MailTemplateService(MailTemplateMapper templateMapper, MailTemplateRenderer renderer) {
        this.templateMapper = templateMapper;
        this.renderer = renderer;
    }

    public IPage<MailTemplatePO> list(int current, int size) {
        return templateMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<MailTemplatePO>().orderByDesc(MailTemplatePO::getCreateTime));
    }

    public MailTemplatePO getById(Long id) { return templateMapper.selectById(id); }

    /** 按编码查询模板（用于代码中引用） */
    public MailTemplatePO getByCode(String code) {
        return templateMapper.selectOne(
                new LambdaQueryWrapper<MailTemplatePO>().eq(MailTemplatePO::getCode, code));
    }

    @Transactional
    public Long create(String name, String code, String subject, String content, String createdBy) {
        MailTemplatePO po = new MailTemplatePO();
        po.setName(name); po.setCode(code); po.setSubject(subject);
        po.setContent(content); po.setStatus("active"); po.setCreatedBy(createdBy);
        po.setCreateTime(LocalDateTime.now()); po.setUpdateTime(LocalDateTime.now());
        templateMapper.insert(po);
        log.info("邮件模板创建成功: id={}, code={}, name={}", po.getId(), code, name);
        return po.getId();
    }

    @Transactional
    public void update(Long id, String name, String subject, String content, String status) {
        MailTemplatePO po = templateMapper.selectById(id);
        if (po == null) throw new RuntimeException("模板不存在");
        if (name != null) po.setName(name);
        if (subject != null) po.setSubject(subject);
        if (content != null) po.setContent(content);
        if (status != null) po.setStatus(status);
        po.setUpdateTime(LocalDateTime.now());
        templateMapper.updateById(po);
        log.info("邮件模板更新成功: id={}", id);
    }

    @Transactional
    public void delete(Long id) {
        templateMapper.deleteById(id);
        log.info("邮件模板删除成功: id={}", id);
    }

    /** 预览模板渲染效果 */
    public Map<String, String> preview(Long id, Map<String, Object> variables) {
        MailTemplatePO po = templateMapper.selectById(id);
        if (po == null) throw new RuntimeException("模板不存在");
        return Map.of(
                "subject", renderer.render(po.getSubject(), variables),
                "content", renderer.render(po.getContent(), variables));
    }
}
