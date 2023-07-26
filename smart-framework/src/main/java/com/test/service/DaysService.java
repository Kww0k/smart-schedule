package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.*;
import com.test.domain.entity.Days;

import java.util.List;


/**
 * 日排班表(Days)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:44:59
 */
public interface DaysService extends IService<Days> {

    ResponseResult selectOneSchedule(Long id);

    ResponseResult getWeekList(ScheduleDto scheduleDto);

    ResponseResult getDayList(ScheduleDto scheduleDto);

    ResponseResult deleteSchedule(Days day);

    ResponseResult addSchedule(AddScheduleDto addScheduleDto);

    ResponseResult generateDaySchedule(GenerateDayDto generateDayDto);

    ResponseResult generateWeekSchedule(GenerateWeekDto generateWeekDto);

    ResponseResult neededMan(Long id, String date);

    ResponseResult readyToWorkMan(Long id, String time, String date);

    ResponseResult resetWeekSchedule(GenerateWeekDto generateWeekDto);

    ResponseResult selectSelfSchedule(Long id);

    ResponseResult addApply(AddApplyDto addApplyDto);

    ResponseResult deleteDaysSchedule(GenerateWeekDto generateWeekDto);

    ResponseResult getDayWork(DayWorkDto dayWorkDto);
}
