package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Dict;
import org.apache.ibatis.annotations.Mapper;


/**
 * (Dict)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-18 16:51:35
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}
