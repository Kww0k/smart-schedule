package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddMenuDto;
import com.test.domain.dto.UpdateMenuDto;
import com.test.domain.entity.Menu;
import com.test.domain.vo.MenuVo;
import com.test.mapper.MenuMapper;
import com.test.service.MenuService;
import com.test.utils.BeanCopyUtils;
import com.test.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * (Menu)表服务实现类
 *
 * @author makejava
 * @since 2022-12-17 14:04:44
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public ResponseResult treeList(String menuName) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(menuName), Menu::getMenuName, menuName);
        List<Menu> menus = baseMapper.selectList(wrapper);
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);
        List<MenuVo> parentNode = menuVos.stream()
                .filter(menuVo -> menuVo.getParentId() == null)
                .toList();
        for (MenuVo menu : parentNode) {
            menu.setChildren(menuVos.stream()
                    .filter(m -> Objects.equals(m.getParentId(), menu.getId()))
                    .toList());
        }
        return ResponseResult.okResult(parentNode);
    }

    @Override
    public ResponseResult addParentMenu(AddMenuDto addMenuDto) {
        Menu menu = BeanCopyUtils.copyBean(addMenuDto, Menu.class);
        save(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteById(Long id) {
        baseMapper.deleteById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteBatch(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateMenu(UpdateMenuDto updateMenuDto) {
        Menu menu = BeanCopyUtils.copyBean(updateMenuDto, Menu.class);
        baseMapper.updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public List<MenuVo> selectTree(Long userId) {
        List<Menu> menus;
        if (SecurityUtils.isAdmin())
            menus = baseMapper.selectList(null);
        else
            menus = baseMapper.selectMenuById(userId);
        List<MenuVo> menuTree = buildTree(menus);
        return menuTree;
    }

    private List<MenuVo> buildTree(List<Menu> menus) {
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);
        List<MenuVo> parentNode = menuVos.stream()
                .filter(menuVo -> menuVo.getParentId() == null)
                .toList();
        for (MenuVo menu : parentNode) {
            menu.setChildren(menuVos.stream()
                    .filter(m -> Objects.equals(m.getParentId(), menu.getId()))
                    .toList());
        }
        return parentNode;
    }
}
