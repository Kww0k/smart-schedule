package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("增加门店的信息的实体类")
public class AddInfoDto {
    //店名
    @ApiModelProperty("店名")
    private String name;
    //地址
    @ApiModelProperty("地址")
    private String address;
    //占地面积
    @ApiModelProperty("占地面积")
    private Double size;
}
