package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.mapper.AiDataMapper;
import com.test.mapper.RuleMapper;
import com.test.mapper.UserMapper;
import com.test.service.AiDataService;
import com.test.service.RoleService;
import com.test.service.RuleService;
import com.test.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/echarts")
@Api(tags = "获取echarts图所要的数据", description = "根据echarts图需要的数据返回数据")
public class EchartsController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AiDataService AiDataService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("ai数据图表")
    @GetMapping("/dateInfo")
    @SystemLog(businessName = "ai数据图表")
    public ResponseResult getAiData(@RequestParam("date") String date) {
        return AiDataService.getAiData(date);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("获取用户图表信息")
    @GetMapping("/members")
    @SystemLog(businessName = "获取用户图表信息")
    public ResponseResult members() {
        return userService.members();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("获取角色图表信息")
    @GetMapping("/roleMember")
    @SystemLog(businessName = "获取角色图表信息")
    public ResponseResult roleMember() {
        return roleService.roleMember();
    }
}
