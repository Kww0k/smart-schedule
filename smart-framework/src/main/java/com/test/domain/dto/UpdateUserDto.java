package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("更新的用户信息")
public class UpdateUserDto {
    @ApiModelProperty("用户id")
    private Long id;

    //姓名
    @ApiModelProperty("姓名")
    private String name;
    //邮件地址
    @ApiModelProperty("邮件地址")
    private String email;
    @ApiModelProperty("电话号码")
    private String phoneNumber;
    //0正常，1停用
    @ApiModelProperty("状态")
    private Integer status;
    //工作日偏好
    @ApiModelProperty("工作日偏好")
    private String dayPreference;
    //工作时长偏好1 上午 2下午 3晚上 空为全天
    @ApiModelProperty("工作时长偏好1 上午 2下午 3晚上 空为全天")
    private String timePreference;
    //日班次时长偏好
    @ApiModelProperty("日班次时长偏好")
    private Double dayTimePreference;
    //周班次时长偏好
    @ApiModelProperty("周班次时长偏好")
    private Double weekTimePreference;

    @ApiModelProperty("门店名称")
    private String store;

    @ApiModelProperty("角色名称")
    private String role;
}
