package com.demetrius.blog.article.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.blog.article.infrastructure.persistence.po.ArticlePO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper extends BaseMapper<ArticlePO> {

    /**
     * 浏览量 +1
     */
    @Update("UPDATE t_article SET view_count = view_count + 1 WHERE id = #{id}")
    void updateViewCount(Long id);

    /**
     * 查询点赞记录数
     */
    @Select("SELECT COUNT(1) FROM t_article_like WHERE article_id = #{articleId} AND user_id = #{userId} AND status = 1")
    Integer checkLikeExists(Long articleId, Long userId);

    /**
     * 取消点赞（硬删除）
     */
    @Delete("DELETE FROM t_article_like WHERE article_id = #{articleId} AND user_id = #{userId}")
    void deleteLike(Long articleId, Long userId);

    /**
     * 新增点赞
     */
    @Insert("INSERT INTO t_article_like(article_id, user_id, status, create_time) VALUES(#{articleId}, #{userId}, 1, NOW())")
    void insertLike(Long articleId, Long userId);
}
