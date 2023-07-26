package com.test.domain.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 申请休息表(Rest)表实体类
 *
 * @author makejava
 * @since 2023-03-14 16:07:32
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_rest")
public class Rest  {
    @TableId
    private Long id;
    private Date date;
    //删除标志（0代表存在 1代表删除）
    private Integer delFlag;
    //创建者
    private Long createBy;
    //创建时间
    private Date createTime;
    //更新者
    private Long updateBy;
    //更新时间
    private Date updateTime;

    private String reason;
    
    private String startTime;
    
    private String endTime;



}
