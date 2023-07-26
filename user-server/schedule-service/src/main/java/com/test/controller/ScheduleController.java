package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddApplyDto;
import com.test.domain.dto.DayWorkDto;
import com.test.service.DaysService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
@Api(tags = "排班的接口", description = "对员工排班进行一系列的操作")
public class ScheduleController {

    @Autowired
    DaysService daysService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取某人单日的排班信息")
    @PostMapping("/getDayWork")
    @SystemLog(businessName = "获取某人单日的排班信息")
    public ResponseResult getDayWork(@RequestBody DayWorkDto dayWorkDto) {
        return daysService.getDayWork(dayWorkDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("查看个人的所有排班信息")
    @GetMapping("/{id}")
    @SystemLog(businessName = "查看个人的所有排班信息")
    public ResponseResult selectOneSchedule(@ApiParam("id") @PathVariable Long id) {
        return daysService.selectOneSchedule(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("获取本周排班信息")
    @GetMapping("/getWeekSchedule")
    @SystemLog(businessName = "获取本周排班信息")
    public ResponseResult selectSelfSchedule(@RequestParam Long id) {
        return daysService.selectSelfSchedule(id);
    }

    @PostMapping("/addApply")
    public ResponseResult addApply(@RequestBody AddApplyDto addApplyDto) {
        return daysService.addApply(addApplyDto);
    }

}
