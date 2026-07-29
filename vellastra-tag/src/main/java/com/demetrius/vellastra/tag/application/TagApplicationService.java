package com.demetrius.vellastra.tag.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.tag.infrastructure.persistence.mapper.ArticleTagMapper;
import com.demetrius.vellastra.tag.infrastructure.persistence.mapper.TagMapper;
import com.demetrius.vellastra.tag.infrastructure.persistence.po.TagPO;
import com.demetrius.vellastra.tag.interfaces.dto.TagVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: TagApplicationService</p>
 * <p>Description: 标签应用服务，提供标签 CRUD、热门标签等业务逻辑</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Service
public class TagApplicationService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    public TagApplicationService(TagMapper tagMapper, ArticleTagMapper articleTagMapper) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
    }

    /**
     * 获取所有标签列表
     */
    public List<TagVO> listAll() {
        return tagMapper.selectList(
                new LambdaQueryWrapper<TagPO>()
                        .eq(TagPO::getStatus, 1)
                        .orderByDesc(TagPO::getArticleCount)
        ).stream().map(this::toVO).toList();
    }

    /**
     * 获取热门标签（按使用次数排序）
     *
     * @param limit 获取数量
     */
    @Cacheable(value = "hotTags", key = "#limit", unless = "#result == null || #result.isEmpty()")
    public List<TagVO> getHotTags(int limit) {
        return tagMapper.selectList(
                new LambdaQueryWrapper<TagPO>()
                        .eq(TagPO::getStatus, 1)
                        .orderByDesc(TagPO::getArticleCount)
                        .last("LIMIT " + limit)
        ).stream().map(this::toVO).toList();
    }

    /**
     * 根据 ID 获取标签
     */
    public TagVO getById(Long id) {
        TagPO po = tagMapper.selectById(id);
        if (po == null) {
            throw ErrorCode.COMMENT_NOT_FOUND.toException();
        }
        return toVO(po);
    }

    /**
     * 创建标签
     */
    @Transactional
    public Long create(String name, String slug) {
        // 检查名称唯一
        Long exists = tagMapper.selectCount(
                new LambdaQueryWrapper<TagPO>().eq(TagPO::getName, name));
        if (exists > 0) {
            throw new BizException(400, "标签名称已存在");
        }
        TagPO po = new TagPO();
        po.setName(name);
        po.setSlug(slug);
        po.setArticleCount(0);
        po.setStatus(1);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        tagMapper.insert(po);
        log.info("新增标签: id={}, name={}", po.getId(), name);
        return po.getId();
    }

    /**
     * 更新标签
     */
    @Transactional
    public void update(Long id, String name, String slug) {
        TagPO po = tagMapper.selectById(id);
        if (po == null) {
            throw ErrorCode.COMMENT_NOT_FOUND.toException();
        }
        po.setName(name);
        po.setSlug(slug);
        po.setUpdateTime(LocalDateTime.now());
        tagMapper.updateById(po);
        log.info("更新标签: id={}", id);
    }

    /**
     * 删除标签（有关联文章时不可删除）
     */
    @Transactional
    public void delete(Long id) {
        TagPO po = tagMapper.selectById(id);
        if (po == null) {
            throw ErrorCode.COMMENT_NOT_FOUND.toException();
        }
        Long refCount = articleTagMapper.selectCount(
                new LambdaQueryWrapper<com.demetrius.vellastra.tag.infrastructure.persistence.po.ArticleTagPO>()
                        .eq(com.demetrius.vellastra.tag.infrastructure.persistence.po.ArticleTagPO::getTagId, id));
        if (refCount > 0) {
            throw new BizException(400, "该标签下有" + refCount + "篇文章关联，无法删除");
        }
        tagMapper.deleteById(id);
        log.info("删除标签: id={}", id);
    }

    private TagVO toVO(TagPO po) {
        TagVO vo = new TagVO();
        vo.setId(po.getId());
        vo.setName(po.getName());
        vo.setSlug(po.getSlug());
        vo.setArticleCount(po.getArticleCount());
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }
}