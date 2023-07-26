package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 用户和排班的关联表(UserScheduling)表实体类
 *
 * @author makejava
 * @since 2022-12-02 18:47:45
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_scheduling")
public class UserScheduling  {
    //角色id@TableId
    private Long userId;
    //排班id@TableId
    private Long schedulingId;

}
