package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.RegisterDto;
import com.test.service.VerifyService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@Api(tags = "用户的接口", description = "对用户信息进行一系列的操作")
public class RegisterController {

    @Autowired
    private VerifyService verifyService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("获取邮箱验证码")
    @GetMapping("/verify")
    @SystemLog(businessName = "获取邮箱验证码")
    public ResponseResult getVerify(@ApiParam("邮箱") @RequestParam("email") String email) {
        return verifyService.getVerify(email);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("注册新用户")
    @PostMapping("/doRegister")
    @SystemLog(businessName = "注册新用户")
    public ResponseResult registerUser(@ApiParam("注册信息") @RequestBody RegisterDto dto) {
        return verifyService.doRegister(dto);
    }
}
