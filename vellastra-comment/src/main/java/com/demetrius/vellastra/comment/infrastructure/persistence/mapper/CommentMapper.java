package com.demetrius.vellastra.comment.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.comment.infrastructure.persistence.po.CommentPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: CommentMapper</p>
 * <p>Description: 评论 Mapper</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {
}
