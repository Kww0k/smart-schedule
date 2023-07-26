package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddMenuDto;
import com.test.domain.dto.UpdateMenuDto;
import com.test.domain.entity.Menu;
import com.test.domain.vo.MenuVo;

import java.util.List;


/**
 * (Menu)表服务接口
 *
 * @author makejava
 * @since 2022-12-17 14:04:44
 */
public interface MenuService extends IService<Menu> {

    ResponseResult treeList(String menuName);

    ResponseResult addParentMenu(AddMenuDto addMenuDto);

    ResponseResult deleteById(Long id);

    ResponseResult deleteBatch(List<Long> ids);

    ResponseResult updateMenu(UpdateMenuDto updateMenuDto);

    List<MenuVo> selectTree(Long userId);
}
