package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 固定规则表(SimpleRule)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:46:42
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("store_simple_rule")
public class SimpleRule  {
    @TableId
    private Long id;

    //中间的休息时长
    private Double wakeTime;
    //一天最多工作时长
    private Double longDay;
    //一周最多工作时长
    private Double longWeek;
    //一次最少工作时间
    private Double singleSmall;
    //一次最多工作时间
    private Double singleTop;
    //午休开始时间
    private Double lunchStart;
    //午休结束时间
    private Double lunchEnd;
    //午休时长
    private Double lunchTime;
    //晚饭开始时间
    private Double dinnerStart;
    //晚餐结束时间
    private Double dinnerEnd;
    //晚餐用时
    private Double dinnerTime;
    //工作日开始时间
    private Double weekStart;
    //工作日结束时间
    private Double weekEnd;
    //周末开门时间
    private Double weekendStart;
    //周末结束时间
    private Double weekendEnd;



}
