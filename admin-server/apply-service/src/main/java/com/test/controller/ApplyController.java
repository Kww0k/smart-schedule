package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.RestDto;
import com.test.domain.entity.Days;
import com.test.domain.entity.Rest;
import com.test.service.DaysService;
import com.test.service.RestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("apply")
public class ApplyController {

    @Autowired
    RestService restService;
    @Autowired
    DaysService daysService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("查看申请")
    @GetMapping("/getApply")
    @SystemLog(businessName = "查看申请")
    public ResponseResult getApply(@ApiParam("页数") Integer pageNum,
                                   @ApiParam("一页有几个") Integer pageSize) {
        return restService.getApply(pageNum, pageSize);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("允许申请")
    @PostMapping("/allowApply")
    @SystemLog(businessName = "允许申请")
    public ResponseResult allowApply(@ApiParam("申请的信息") @RequestBody RestDto rest) {
        return restService.allowApply(rest);
    }
}
