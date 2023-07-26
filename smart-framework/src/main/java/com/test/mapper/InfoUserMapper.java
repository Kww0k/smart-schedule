package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.InfoUser;
import com.test.domain.entity.UserScheduling;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 门店与用户的关联表(InfoUser)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:44:30
 */
@Mapper
public interface InfoUserMapper extends BaseMapper<InfoUser> {


}
