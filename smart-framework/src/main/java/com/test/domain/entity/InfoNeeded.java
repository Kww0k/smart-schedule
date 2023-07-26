package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (InfoNeeded)表实体类
 *
 * @author makejava
 * @since 2023-01-19 14:14:43
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("info_needed")
public class InfoNeeded  {
    //门店id@TableId
    private Long infoId;
    //需要员工表的对应id@TableId
    private Long neededId;




}
