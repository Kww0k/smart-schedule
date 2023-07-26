package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 角色的菜单表(RoleMenu)表实体类
 *
 * @author makejava
 * @since 2022-12-18 20:20:57
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role_menu")
public class RoleMenu  {
    //角色id@TableId
    private Long roleId;
    //菜单id@TableId
    private Long menuId;




}
