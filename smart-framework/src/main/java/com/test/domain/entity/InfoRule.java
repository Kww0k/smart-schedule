package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 门店信息与自定义规则的关联表(InfoRule)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:45:50
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("store_info_rule")
public class InfoRule  {
    //门店信息@TableId
    private Long infoId;
    //自定义规则信息@TableId
    private Long ruleId;




}
