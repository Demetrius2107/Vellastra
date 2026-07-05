package com.demetrius.blog.article.interfaces.dto;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: wanqiu
 * @date: 2026-07-04 23:06:08
 * @version: 1.0
 */
@Data
public class SearchArticleRequest {

    private Long id;

    private List<String> tag;

}
