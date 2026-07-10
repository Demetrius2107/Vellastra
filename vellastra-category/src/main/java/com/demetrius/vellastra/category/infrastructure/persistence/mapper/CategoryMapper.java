package com.demetrius.vellastra.category.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.category.infrastructure.persistence.po.CategoryPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: CategoryMapper</p>
 * <p>Description: 分类 Mapper（MyBatis-Plus）</p>
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
public interface CategoryMapper extends BaseMapper<CategoryPO> {
}
