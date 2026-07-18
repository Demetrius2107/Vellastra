package com.demetrius.vellastra.article.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.article.infrastructure.persistence.po.ArticlePO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>Title: ArticleMapper</p>
 * <p>Description: 文章 Mapper（MyBatis-Plus）</p>
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
public interface ArticleMapper extends BaseMapper<ArticlePO> {

    /**
     * 浏览量 +1
     */
    @Update("UPDATE blog_article SET view_count = view_count + 1 WHERE id = #{id}")
    void updateViewCount(Long id);

    /**
     * 查询点赞记录数
     */
    @Select("SELECT COUNT(1) FROM blog_article WHERE article_id = #{articleId} AND user_id = #{userId} AND status = 1")
    Integer checkLikeExists(Long articleId, Long userId);

    /**
     * 取消点赞（硬删除）
     */
    @Delete("DELETE FROM blog_article WHERE article_id = #{articleId} AND user_id = #{userId}")
    void deleteLike(Long articleId, Long userId);

    /**
     * 新增点赞
     */
    @Insert("INSERT INTO t_article_like(article_id, user_id, status, create_time) VALUES(#{articleId}, #{userId}, 1, NOW())")
    void insertLike(Long articleId, Long userId);
}
