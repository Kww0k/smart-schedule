package com.test.domain.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 记录每个时段还需要多少员工的表(Needed)表实体类
 *
 * @author makejava
 * @since 2023-01-19 14:18:05
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("scheduling_needed")
public class Needed  {
    //id
    @TableId
    private Long id;

    //日期
    private Date date;
    //需要员工的时间
    private Double time;
    //需要员工的个数
    private Integer needMan;
    //逻辑删除
    private Integer delFlag;
    
    private Long createBy;
    
    private Date createTime;
    
    private Long updateBy;
    
    private Date updateTime;



}
