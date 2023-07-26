package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("更新门店的信息的实体类")
public class UpdateInfoDto {
    @ApiModelProperty("门店id")
    private Long id;
    //店名
    @ApiModelProperty("店名")
    private String name;
    //地址
    @ApiModelProperty("地址")
    private String address;
    //占地面积
    @ApiModelProperty("占地面积")
    private Double size;
    //早到的职位id
    @ApiModelProperty("早到的职位id")
    private String doMorning;
    //可以少做的职位id
    @ApiModelProperty("可以少做的职位id")
    private String doLess;
    //打扫卫生的职位id
    @ApiModelProperty("打扫卫生的职位id")
    private String doLater;
}
