package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Article;
import org.apache.ibatis.annotations.Mapper;


/**
 * (Article)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-11 17:19:22
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
