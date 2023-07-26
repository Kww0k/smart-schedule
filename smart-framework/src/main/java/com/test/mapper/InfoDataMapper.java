package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.InfoData;
import org.apache.ibatis.annotations.Mapper;


/**
 * (InfoData)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-15 03:36:03
 */
@Mapper
public interface InfoDataMapper extends BaseMapper<InfoData> {

}
