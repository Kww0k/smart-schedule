package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.entity.User;
import com.test.service.VerifyService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "登陆接口", description = "有关用户登陆授权等一系列操作")
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private VerifyService verifyService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "登陆成功"),
            @ApiResponse(code = 401, message = "需要登陆"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @ApiOperation("用户登陆")
    @PostMapping("/login")
    @SystemLog(businessName = "用户登陆")
    public ResponseResult login(@ApiParam("用户信息")@RequestBody User user) {
        return verifyService.login(user);
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取路由信息")
    @GetMapping("/getRouter")
    @SystemLog(businessName = "获取路由信息")
    public ResponseResult getRouter() {
        return verifyService.getRouter();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "退出成功")
    })
    @ApiOperation("退出登陆")
    @PostMapping("/logout")
    @SystemLog(businessName = "退出登陆")
    public ResponseResult logout() {
        return verifyService.logout();
    }
}
