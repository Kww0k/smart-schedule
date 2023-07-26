package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddArticleDto;
import com.test.domain.dto.UpdateArticleDto;
import com.test.domain.entity.Article;
import com.test.domain.entity.Role;
import com.test.domain.entity.User;
import com.test.domain.entity.UserRole;
import com.test.domain.vo.ArticleInfoVo;
import com.test.domain.vo.PageArticleVo;
import com.test.domain.vo.PageRoleVo;
import com.test.domain.vo.RoleInfoVo;
import com.test.mapper.ArticleMapper;
import com.test.mapper.UserMapper;
import com.test.service.ArticleService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * (Article)表服务实现类
 *
 * @author makejava
 * @since 2023-01-11 17:19:24
 */
@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, String name) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Article::getName, name);
        Long count = baseMapper.selectCount(wrapper);
        Page<Article> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<ArticleInfoVo> articleInfoVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleInfoVo.class);
        for (ArticleInfoVo articleInfoVo : articleInfoVos) {
            LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(User::getId, articleInfoVo.getCreateBy());
            articleInfoVo.setUser(userMapper.selectOne(wrapper1).getName());
        }
        PageArticleVo pageArticleVo = new PageArticleVo(count, articleInfoVos);
        return ResponseResult.okResult(pageArticleVo);
    }

    @Override
    public ResponseResult addArticle(AddArticleDto addArticleDto) {
        Article article = BeanCopyUtils.copyBean(addArticleDto, Article.class);
        baseMapper.insert(article);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateArticleInfo(UpdateArticleDto updateArticleDto) {
        Article article = BeanCopyUtils.copyBean(updateArticleDto, Article.class);
        baseMapper.updateById(article);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteArticleById(Long id) {
        baseMapper.deleteById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteBatch(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult articlePage(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Article::getId);
        Long count = baseMapper.selectCount(null);
        Page<Article> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<ArticleInfoVo> articleInfoVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleInfoVo.class);
        for (ArticleInfoVo articleInfoVo : articleInfoVos) {
            LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(User::getId, articleInfoVo.getCreateBy());
            articleInfoVo.setUser(userMapper.selectOne(wrapper1).getName());
        }
        PageArticleVo pageArticleVo = new PageArticleVo(count, articleInfoVos);
        return ResponseResult.okResult(pageArticleVo);
    }

    @Override
    public ResponseResult selectById(Long id) {
        Article article = baseMapper.selectById(id);
        ArticleInfoVo articleInfoVo = BeanCopyUtils.copyBean(article, ArticleInfoVo.class);
        articleInfoVo.setUser(userMapper.selectById(articleInfoVo.getCreateBy()).getName());;
        return ResponseResult.okResult(articleInfoVo);
    }
}
