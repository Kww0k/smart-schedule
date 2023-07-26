package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 用户与角色的关联表(UserRole)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:47:27
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user_role")
public class UserRole  {
    //用户信息id@TableId
    private Long userId;
    //角色信息id@TableId
    private Long roleId;




}
