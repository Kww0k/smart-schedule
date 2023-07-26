package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddRuleDto;
import com.test.domain.dto.UpdateRuleDto;
import com.test.service.InfoRuleService;
import com.test.service.RuleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rule")
@Api(tags = "规则信息接口", description = "管理员对于规则信息的增删改查")
public class RuleController {

    @Autowired
    private RuleService ruleService;
    @Autowired
    private InfoRuleService infoRuleService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("查询所有规则")
    @GetMapping("/list")
    @SystemLog(businessName = "查询所有规则")
    public ResponseResult getRuleList() {
        return ruleService.getRuleList();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 404, message = "没有这个规则")
    })
    @ApiOperation("根据id查找规则信息")
    @GetMapping("/findById")
    @SystemLog(businessName = "根据id查找规则信息")
    public ResponseResult getRuleById(@ApiParam("规则id") @RequestParam Long id) {
        return ruleService.getRuleById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "增加成功"),
            @ApiResponse(code = 400, message = "传参有误")
    })
    @ApiOperation("增加一个规则的信息")
    @PostMapping("/addStore")
    @SystemLog(businessName = "增加一个规则的信息")
    public ResponseResult addRule(@ApiParam("新增规则的信息") @RequestBody AddRuleDto addRuleDto) {
        return ruleService.addRule(addRuleDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "增加成功"),
            @ApiResponse(code = 404, message = "规则不存在")
    })
    @ApiOperation("删除规则信息")
    @DeleteMapping("/deleteRule")
    @SystemLog(businessName = "删除规则信息")
    public ResponseResult deleteRuleById(@ApiParam("规则id") @RequestParam("id") Long id) {
        return ruleService.deleteRuleById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("更新规则信息")
    @PutMapping("/updateRule")
    @SystemLog(businessName = "更新规则信息")
    public ResponseResult updateRuleById(@ApiParam("更新规则的信息") @RequestBody UpdateRuleDto updateRuleDto) {
        return ruleService.updateRuleById(updateRuleDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("根据门店id查询规则")
    @GetMapping("/findByStoreId")
    @SystemLog(businessName = "根据门店id查询规则")
    public ResponseResult findByStoreId(@ApiParam("门店id")@RequestParam("id") Long id) {
        return infoRuleService.findByStoreId(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("查询所有门店")
    @GetMapping("/findAllStore")
    @SystemLog(businessName = "查询所有门店")
    public ResponseResult findAllStore() {
        return ruleService.findAllStore();
    }

}
