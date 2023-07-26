package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (InfoData)表实体类
 *
 * @author makejava
 * @since 2023-01-15 03:36:04
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("info_data")
public class InfoData  {
    //门店id@TableId
    private Long infoId;
    //ai数据id@TableId
    private Long dataId;




}
