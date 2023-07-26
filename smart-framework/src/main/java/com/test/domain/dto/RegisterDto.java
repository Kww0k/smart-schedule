package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("注册用户")
public class RegisterDto {
    @ApiModelProperty("用户名")
    String name;
    @ApiModelProperty("邮箱")
    String email;
    @ApiModelProperty("密码")
    String password;
    @ApiModelProperty("门店码")
    Integer storeCode;
    @ApiModelProperty("验证码")
    String code;
}
