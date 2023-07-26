package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * (Menu)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-17 14:04:42
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectMenuById(Long userId);

}
