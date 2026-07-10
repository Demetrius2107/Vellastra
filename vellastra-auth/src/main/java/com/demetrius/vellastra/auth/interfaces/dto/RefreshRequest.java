package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

/**
 * <p>Title: RefreshRequest</p>
 * <p>Description: 刷新token请求 DTO</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年07月05日 首次创建
 * @date 2026年07月05日 最后修改
 */
@Data
public class RefreshRequest {

    private String token;
}
