package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.entity.RoleMenu;

import java.util.List;


/**
 * 角色的菜单表(RoleMenu)表服务接口
 *
 * @author makejava
 * @since 2022-12-18 20:20:57
 */
public interface RoleMenuService extends IService<RoleMenu> {

    ResponseResult menu(Long roleId, List<Long> menuIds);

    ResponseResult getRoleMenu(Long roleId);
}
