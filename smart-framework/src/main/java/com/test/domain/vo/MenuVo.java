package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuVo {
    private Long id;

    //菜单名称
    private String menuName;
    //父菜单id

    private Long parentId;
    //路由地址
    private String path;
    private String component;
    //菜单状态

    private String status;
    //权限标识
    private String perms;
    //菜单图标
    private String icon;

    private List<MenuVo> children;
}
