package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.*;
import com.test.domain.entity.Days;
import com.test.service.DaysService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
@Api(tags = "排班信息接口", description = "管理员对于排班信息的增删改查")
public class ScheduleController {

    @Autowired
    private DaysService daysService;


    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取一周的排班信息")
    @PostMapping("/weekList")
    @SystemLog(businessName = "获取一周的排班")
    public ResponseResult getWeekList(@ApiParam("需要查询的信息") @RequestBody ScheduleDto scheduleDto) {
        return daysService.getWeekList(scheduleDto);
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("删除排班")
    @PostMapping("/deleteDaysSchedule")
    @SystemLog(businessName = "删除排班")
    public ResponseResult deleteDaysSchedule(@ApiParam("需要重置的信息") @RequestBody GenerateWeekDto generateWeekDto) {
        return daysService.deleteDaysSchedule(generateWeekDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取当天的排班信息")
    @PostMapping("/dayList")
    @SystemLog(businessName = "获取当天的排班")
    public ResponseResult getDayList(@ApiParam("需要查询的信息") @RequestBody ScheduleDto scheduleDto) {
        return daysService.getDayList(scheduleDto);
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "生成成功")
    })
    @ApiOperation("生成一天的排班")
    @PostMapping("/generateDaySchedule")
    @SystemLog(businessName = "生成一天的排班")
    public ResponseResult generateDaySchedule(@ApiParam("生成的信息")@RequestBody  GenerateDayDto generateDayDto) {
        return daysService.generateDaySchedule(generateDayDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "生成成功")
    })
    @ApiOperation("生成一周的排班")
    @PostMapping("/generateWeekSchedule")
    @SystemLog(businessName = "生成一周的排班")
    public ResponseResult generateWeekSchedule(@ApiParam("门店id和要生成的日期") @RequestBody GenerateWeekDto generateWeekDto) {
        return daysService.generateWeekSchedule(generateWeekDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取可以安排排班的信息")
    @GetMapping("/neededMan")
    @SystemLog(businessName = "获取可以安排排班的信息")
    public ResponseResult neededMan(@ApiParam("门店id") @RequestParam("storeId") Long id, @ApiParam("要查看的日期") @RequestParam("date") String date) {
        return daysService.neededMan(id, date);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取可以安排进排班的人的信息")
    @GetMapping("/readyToWorkMan")
    @SystemLog(businessName = "获取可以安排进排班的人的信息")
    public ResponseResult readyToWorkMan(@ApiParam("门店id") @RequestParam("storeId") Long id,
                                         @ApiParam("时间") @RequestParam("time") String time,
                                         @ApiParam("要查看的日期") @RequestParam("date") String date) {
        return daysService.readyToWorkMan(id, time, date);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("给人指派排班")
    @PostMapping("/addSchedule")
    @SystemLog(businessName = "给人指派排班")
    public ResponseResult addSchedule(@ApiParam("需要新增的信息") @RequestBody AddScheduleDto addScheduleDto) {
        return daysService.addSchedule(addScheduleDto);
    }
}
