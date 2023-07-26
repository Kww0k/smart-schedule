package com.test.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
/**
 * 用户表(User)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:47:12
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class User  {
    //id可以对应查看员工的门店，职位和偏好
    @TableId
    private Long id;

    //姓名
    private String name;
    //邮件地址
    private String email;
    //账号的密码
    private String password;
    //手机号
    private String phoneNumber;
    //0正常，1停用
    private Integer status;
    //工作日偏好
    private String dayPreference;
    //工作时长偏好1 上午 2下午 3晚上 空为全天
    private String timePreference;
    //日班次时长偏好
    private Double dayTimePreference;
    //周班次时长偏好
    private Double weekTimePreference;
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
