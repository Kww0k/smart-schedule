package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("新安排的员工排班")
public class AddScheduleDto {

    @ApiModelProperty("员工id")
    private Long id;
    @ApiModelProperty("门店id")
    private Long storeId;
    @ApiModelProperty("添加的日期")
    private String date;
    @ApiModelProperty("开始时间")
    private String time;
    @ApiModelProperty("添加的工作时间")
    private int workTime;
}
