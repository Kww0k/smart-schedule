package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户与角色的关联表(UserRole)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:47:27
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
