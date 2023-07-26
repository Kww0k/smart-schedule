package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Files;
import org.apache.ibatis.annotations.Mapper;


/**
 * (File)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-12 14:06:18
 */
@Mapper
public interface FileMapper extends BaseMapper<Files> {

}
