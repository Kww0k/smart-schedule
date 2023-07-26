package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("更新角色的信息的实体类")
public class UpdateRoleDto {

    @ApiModelProperty("id")
    private Long id;

    //角色名称
    @ApiModelProperty("角色名称")
    private String roleName;
    //角色权限字符串
    @ApiModelProperty("角色权限字符串")
    private String roleKey;
    //标注
    @ApiModelProperty("标注")
    private String remark;
    //0为可用，1为停用
    @ApiModelProperty("角色状态")
    private Integer status;
}
