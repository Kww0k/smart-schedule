package com.test.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 用户和申请信息的关联表(UserRest)表实体类
 *
 * @author makejava
 * @since 2023-03-14 16:07:13
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_rest")
public class UserRest  {
    private Long userId;
    private Long restId;




}
