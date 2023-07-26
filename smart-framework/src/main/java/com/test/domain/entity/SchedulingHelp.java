package com.test.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulingHelp {

    private Long userId;
    private Long roleId;
    private String name;

    private int status;

    private Double workTime;

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

    private Double dayTime;

    private Double weekTime;
    //工作日偏好
    private String dayPreference;
    //工作时长偏好1 上午 2下午 3晚上 空为全天
    private String timePreference;
    //日班次时长偏好
    private Double dayTimePreference;
    //周班次时长偏好
    private Double weekTimePreference;

    private int dayStatus;
    private int weekStatus;
}
