package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.service.ArticleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
@Api(tags = "通知接口", description = "查看通知")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("分页查询所有文章信息")
    @GetMapping("/page")
    @SystemLog(businessName = "分页查询所有文章信息")
    public ResponseResult articleList(@ApiParam("页数") Integer pageNum,
                                   @ApiParam("一页有几个") Integer pageSize) {
        return articleService.articlePage(pageNum, pageSize);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("根据id查询文章内容")
    @GetMapping("{id}")
    @SystemLog(businessName = "根据id查询文章内容")
    public ResponseResult selectById(@ApiParam("id") @PathVariable Long id) {
        return articleService.selectById(id);
    }
}
