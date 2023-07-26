package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 门店与用户的关联表(InfoUser)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:44:30
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("info_user")
public class InfoUser  {
    //店面信息id@TableId
    private Long infoId;
    //用户信息id@TableId
    private Long userId;




}
