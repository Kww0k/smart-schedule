package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Info;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 门店信息(Info)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:45:36
 */
@Mapper
public interface InfoMapper extends BaseMapper<Info> {

}
