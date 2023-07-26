package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddArticleDto;
import com.test.domain.dto.AddRoleDto;
import com.test.domain.dto.UpdateArticleDto;
import com.test.domain.dto.UpdateRoleDto;
import com.test.service.ArticleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@Api(tags = "公告接口", description = "对公告进行一系列操作")
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
                                   @ApiParam("一页有几个") Integer pageSize,
                                   @ApiParam("文章名称") String name) {
        return articleService.articleList(pageNum, pageSize, name);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 404, message = "没有这个文章")
    })
    @ApiOperation("根据id删除文章信息")
    @DeleteMapping("/deleteById")
    @SystemLog(businessName = "根据id删除角色信息")
    public ResponseResult deleteArticleById(@ApiParam("id")@RequestParam Long id) {
        return articleService.deleteArticleById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("根据id批量删除文章信息")
    @PostMapping("/deleteBatch")
    @SystemLog(businessName = "根据id批量删除文章信息")
    public ResponseResult deleteBatch(@ApiParam("ids") @RequestBody List<Long> ids) {
        return articleService.deleteBatch(ids);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @ApiOperation("更新文章信息")
    @PostMapping ("/updateArticle")
    @SystemLog(businessName = "更新文章信息")
    public ResponseResult updateArticleInfo(@ApiParam("更新后的信息") @RequestBody UpdateArticleDto updateArticleDto) {
        return articleService.updateArticleInfo(updateArticleDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "增加成功"),
            @ApiResponse(code = 400, message = "缺少参数"),
            @ApiResponse(code = 404, message = "插入时出错"),
    })
    @ApiOperation("增加一个文章")
    @PutMapping("/addArticle")
    @SystemLog(businessName = "增加一个文章")
    public ResponseResult addArticle(@ApiParam("新增的信息") @RequestBody AddArticleDto addArticleDto) {
        return articleService.addArticle(addArticleDto);
    }
}
