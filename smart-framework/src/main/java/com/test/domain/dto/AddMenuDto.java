package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("增加菜单的信息的实体类")
public class AddMenuDto {

    @ApiModelProperty("id")
    private Long id;

    //菜单名称
    @ApiModelProperty("菜单名称")
    private String menuName;

    //父菜单id
    @ApiModelProperty("父菜单id")
    private Long parentId;
    //路由地址
    @ApiModelProperty("路由地址")
    private String path;
    @ApiModelProperty("组件地址")
    private String component;
    //菜单状态
    @ApiModelProperty("菜单状态")
    private String status;
    //菜单图标
    @ApiModelProperty("菜单图标")
    private String icon;
}
