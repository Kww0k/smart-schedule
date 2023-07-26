package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("新增的用户信息")
public class AddUserDto {
    //姓名
    @ApiModelProperty("姓名")
    private String name;
    //邮件地址
    @ApiModelProperty("邮件地址")
    private String email;
    //账号的密码
    @ApiModelProperty("账号的密码")
    private String password;
    //手机号
    @ApiModelProperty("手机号")
    private String phoneNumber;
    //工作日偏好
    @ApiModelProperty("工作日偏好")
    private String dayPreference;
    //工作时长偏好1 上午 2下午 3晚上 空为全天
    @ApiModelProperty("工作日偏好")
    private String timePreference;
    //日班次时长偏好
    @ApiModelProperty("日班次时长偏好")
    private Double dayTimePreference;
    //周班次时长偏好
    @ApiModelProperty("周班次时长偏好")
    private Double weekTimePreference;

    @ApiModelProperty("门店id")
    private Long store;

    @ApiModelProperty("角色id")
    private Long role;
}
