package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Rest;
import com.test.domain.entity.UserRest;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;


/**
 * 用户和申请信息的关联表(UserRest)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-14 16:07:12
 */
@Mapper
public interface UserRestMapper extends BaseMapper<UserRest> {

    String selectNameById(Long id);

    List<Rest> selectInfo(Long id, Date date);

    List<UserRest> selectDate(Long id, String startTime, String endTime);
}
