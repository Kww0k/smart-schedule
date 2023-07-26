package com.test.domain.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (Menu)表实体类
 *
 * @author makejava
 * @since 2022-12-17 14:04:44
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_menu")
public class Menu  {
    //菜单id
    @TableId
    private Long id;

    //菜单名称
    private String menuName;
    //父菜单id

    private Long parentId;
    private String component;
    //路由地址
    private String path;
    //菜单状态
    private String status;
    //权限标识
    private String perms;
    //菜单图标
    private String icon;
    //删除标志（0代表存在 1代表删除）
    private Integer delFlag;
    //创建者
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    //更新者
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;



}
