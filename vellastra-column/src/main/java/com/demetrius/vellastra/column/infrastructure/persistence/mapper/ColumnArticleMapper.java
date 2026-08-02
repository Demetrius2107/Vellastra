package com.demetrius.vellastra.column.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnArticlePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ColumnArticleMapper extends BaseMapper<ColumnArticlePO> {
    @Select("SELECT * FROM t_column_article WHERE column_id = #{columnId} ORDER BY sort_order ASC, create_time DESC")
    List<ColumnArticlePO> findByColumnId(Long columnId);

    @Select("SELECT COUNT(*) FROM t_column_article WHERE column_id = #{columnId}")
    int countByColumnId(Long columnId);
}
