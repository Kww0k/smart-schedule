package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleVo {

    private Long id;

    private Date date;
    //早上开始时间
    private Double morningStart;
    //早上结束时间
    private Double morningEnd;
    //早上第二班开始时间
    private Double morningSecondStart;
    //早上第二班结束时间
    private Double morningSecondEnd;
    //下午开始时间
    private Double afternoonStart;
    //下午结束时间
    private Double afternoonEnd;
    //下午第二班开始时间
    private Double afternoonSecondStart;
    //下午第二班结束时间
    private Double afternoonSecondEnd;
    //晚上开始时间
    private Double eveningStart;
    //晚上结束时间
    private Double eveningEnd;
    //晚上第二班开始时间
    private Double eveningSecondStart;
    //晚上第二班结束时间
    private Double eveningSecondEnd;
}
