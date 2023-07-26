package com.test.domain.entity;

import java.util.Date;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 日排班表(Days)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:44:59
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("scheduling_days")
public class Days {
    @TableId
    private Long id;
    //日期
    private Date date;
    //早上开始时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double morningStart;
    //早上结束时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double morningEnd;
    //早上第二班开始时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double morningSecondStart;
    //早上第二班结束时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double morningSecondEnd;
    //下午开始时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double afternoonStart;
    //下午结束时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double afternoonEnd;
    //下午第二班开始时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double afternoonSecondStart;
    //下午第二班结束时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double afternoonSecondEnd;
    //晚上开始时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double eveningStart;
    //晚上结束时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double eveningEnd;
    //晚上第二班开始时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double eveningSecondStart;
    //晚上第二班结束时间

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double eveningSecondEnd;
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


}
