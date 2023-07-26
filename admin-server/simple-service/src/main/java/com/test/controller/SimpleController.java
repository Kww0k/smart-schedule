package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.service.SimpleRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simple")
@Api(tags = "查看固定规则", description = "固定规则无法修改智能查看")
public class SimpleController {
    @Autowired
    private SimpleRuleService simpleRuleService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查看成功")
    })
    @ApiOperation("查看固定规则")
    @GetMapping("/rule")
    @SystemLog(businessName = "查看固定规则")
    public ResponseResult getSimpleRule() {
        return simpleRuleService.getSimpleRule();
    }
}
