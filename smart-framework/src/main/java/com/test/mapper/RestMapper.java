package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Rest;
import org.apache.ibatis.annotations.Mapper;


/**
 * 申请休息表(Rest)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-14 16:07:32
 */
@Mapper
public interface RestMapper extends BaseMapper<Rest> {

}
