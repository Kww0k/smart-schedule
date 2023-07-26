package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (Dict)表实体类
 *
 * @author makejava
 * @since 2022-12-18 16:51:36
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_dict")
public class Dict  {
    //id
    @TableId
    private Integer id;

    //名称
    private String name;
    //内容
    private String value;
    //类型
    private String tpye;



}
