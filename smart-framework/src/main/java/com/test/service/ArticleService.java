package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddArticleDto;
import com.test.domain.dto.UpdateArticleDto;
import com.test.domain.entity.Article;

import java.util.List;


/**
 * (Article)表服务接口
 *
 * @author makejava
 * @since 2023-01-11 17:19:23
 */
public interface ArticleService extends IService<Article> {

    ResponseResult articleList(Integer pageNum, Integer pageSize, String name);

    ResponseResult addArticle(AddArticleDto addArticleDto);

    ResponseResult updateArticleInfo(UpdateArticleDto updateArticleDto);

    ResponseResult deleteArticleById(Long id);

    ResponseResult deleteBatch(List<Long> ids);

    ResponseResult articlePage(Integer pageNum, Integer pageSize);

    ResponseResult selectById(Long id);
}
