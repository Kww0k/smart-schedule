package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;


/**
 * 角色表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:46:58
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    String selectRoleName(Long id);
}
