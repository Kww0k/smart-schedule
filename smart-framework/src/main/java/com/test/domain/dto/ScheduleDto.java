package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("查询排班")
public class ScheduleDto {
    @ApiModelProperty("门店id")
    private Long storeId;
    @ApiModelProperty("角色id")
    private Long roleId;
    @ApiModelProperty("日期")
    private String date;
    @ApiModelProperty("姓名")
    private String name;
}
