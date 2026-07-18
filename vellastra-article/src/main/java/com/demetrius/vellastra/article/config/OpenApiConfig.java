package com.demetrius.vellastra.article.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>Knife4j / OpenAPI 文档配置</h3>
 *
 * <p>定义 API 文档的标题、描述、版本、联系人等信息，
 * 访问 <a href="http://localhost:8083/doc.html">/doc.html</a> 查看。</p>
 *
 * @author wanqiu
 * @version 1.0
 * @since 2026-07-18
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "星垂野内容系统 - 文章服务",
                description = """
                        ## 文章服务 API
                        
                        提供文章的全生命周期管理功能，包括：
                        
                        * **文章 CRUD** — 创建、编辑、删除、查询
                        * **状态管理** — 草稿 / 发布 / 撤回 / 下架
                        * **互动功能** — 置顶、浏览计数、点赞
                        * **批量操作** — 批量删除和发布
                        
                        ---
                        **基础路径:** `/article`
                        """,
                version = "1.0.0",
                contact = @Contact(
                        name = "wanqiu",
                        email = "admin@vellastra.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        )
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "输入 JWT Token（不带 Bearer 前缀）"
)
public class OpenApiConfig {
}