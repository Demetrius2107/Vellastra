package com.demetrius.vellastra.comment.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.comment.infrastructure.persistence.po.CommentPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: CommentMapper</p>
 * <p>Description: 评论 Mapper</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {
}
