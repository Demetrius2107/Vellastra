package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: TokenVO</p>
 * <p>Description: token 视图对象</p>
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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {

    private String token;
    private Long expireIn;
}
