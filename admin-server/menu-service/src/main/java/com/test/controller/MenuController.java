package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddMenuDto;
import com.test.domain.dto.UpdateMenuDto;
import com.test.service.DictService;
import com.test.service.MenuService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@Api(tags = "菜单接口", description = "有关菜单的一系列操作")
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private DictService dictService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取图标信息")
    @GetMapping("/iconList")
    @SystemLog(businessName = "获取图标信息")
    public ResponseResult getIconList() {
        return dictService.getIconList();
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("查询所有菜单")
    @GetMapping("/list")
    @SystemLog(businessName = "查询所有菜单")
    public ResponseResult list(@ApiParam("菜单名称") @RequestParam String menuName) {
        return menuService.treeList(menuName);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("新增菜单")
    @SystemLog(businessName = "新增菜单")
    @PutMapping ("/addMenu")
    public ResponseResult addParentMenu(@ApiParam("新增的菜单信息") @RequestBody AddMenuDto addMenuDto) {
        return menuService.addParentMenu(addMenuDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("根据id删除")
    @SystemLog(businessName = "根据id删除")
    @DeleteMapping("/deleteById")
    public ResponseResult deleteById(@ApiParam("id") @RequestParam Long id) {
        return menuService.deleteById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("根据id批量删除菜单信息")
    @PostMapping("/deleteBatch")
    @SystemLog(businessName = "根据id批量删除菜单信息")
    public ResponseResult deleteBatch(@ApiParam("ids") @RequestBody List<Long> ids) {
        return menuService.deleteBatch(ids);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @ApiOperation("更新菜单信息")
    @PostMapping("/updateMenu")
    @SystemLog(businessName = "更新菜单信息")
    public ResponseResult updateMenu(@ApiParam("更新的信息") @RequestBody UpdateMenuDto updateMenuDto) {
        return menuService.updateMenu(updateMenuDto);
    }
}
