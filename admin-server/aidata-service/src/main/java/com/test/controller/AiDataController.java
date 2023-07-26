package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.entity.AiData;
import com.test.service.AiDataService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;

@RestController
@RequestMapping("/aiData")
@Api(tags = "查询客流量", description = "看当天的客流量")
public class AiDataController {

    @Autowired
    private AiDataService aiDataService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("根据日期查询客流量")
    @GetMapping("/data/{date}")
    @SystemLog(businessName = "根据日期查询客流量")
    public ResponseResult getAiDataByDate(@ApiParam("查询的日期") @PathVariable("date") String date) {
        return aiDataService.getAiDataByDate(date);
    }

}
