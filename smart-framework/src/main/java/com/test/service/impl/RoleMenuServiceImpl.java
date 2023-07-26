package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Menu;
import com.test.domain.entity.RoleMenu;
import com.test.mapper.MenuMapper;
import com.test.mapper.RoleMenuMapper;
import com.test.service.RoleMenuService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色的菜单表(RoleMenu)表服务实现类
 *
 * @author makejava
 * @since 2022-12-18 20:20:57
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    @Transactional
    public ResponseResult menu(Long roleId, List<Long> menuIds) {
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        baseMapper.delete(wrapper);
        for (Long menuId : menuIds) baseMapper.insert(new RoleMenu(roleId, menuId));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getRoleMenu(Long roleId) {
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = baseMapper.selectList(wrapper);
        List<Long> menuIds = new ArrayList<>();
        // TODO 判断是否有子菜单
        for (RoleMenu roleMenu: roleMenus)
            if ((roleMenu.getMenuId() != 2L) && (roleMenu.getMenuId() != 3L) && (roleMenu.getMenuId() != 4L))
                menuIds.add(roleMenu.getMenuId());
        return ResponseResult.okResult(menuIds);
    }
}
