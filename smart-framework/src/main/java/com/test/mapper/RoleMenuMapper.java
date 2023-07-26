package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;


/**
 * 角色的菜单表(RoleMenu)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-18 20:20:57
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

}
