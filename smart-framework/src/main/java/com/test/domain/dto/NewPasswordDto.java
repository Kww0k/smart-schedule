package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("修改密码")
public class NewPasswordDto {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("原密码")
    private String password;
    @ApiModelProperty("新密码")
    private String newPassword;
    @ApiModelProperty("确认密码")
    private String confirmPassword;
}
