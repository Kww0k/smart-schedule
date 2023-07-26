package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageArticleVo {
    private Long total;

    private List<ArticleInfoVo> articleInfoVos;
}
