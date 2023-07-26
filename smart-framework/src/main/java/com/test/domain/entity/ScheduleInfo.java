package com.test.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleInfo {
    private Long id;
    private String name;

    private String roleName;

    //日期
    private Date date;
    //早上开始时间
    private String morningStart;
    //早上结束时间
    private String morningEnd;
    //早上第二班开始时间
    private String morningSecondStart;
    //早上第二班结束时间
    private String morningSecondEnd;
    //下午开始时间
    private String afternoonStart;
    //下午结束时间
    private String afternoonEnd;
    //下午第二班开始时间
    private String afternoonSecondStart;
    //下午第二班结束时间
    private String afternoonSecondEnd;
    //晚上开始时间
    private String eveningStart;
    //晚上结束时间
    private String eveningEnd;
    //晚上第二班开始时间
    private String eveningSecondStart;
    //晚上第二班结束时间
    private String eveningSecondEnd;
}
