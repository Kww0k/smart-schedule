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
 * 门店信息(Info)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:45:36
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("store_info")
public class Info  {
    @TableId
    private Long id;

    //店名
    private String name;
    //地址
    private String address;
    //占地面积
    private Double size;
    
    private Integer code;
    //早到的职位id
    private String doMorning;
    //可以少做的职位id
    private String doLess;
    //打扫卫生的职位id
    private String doLater;
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
