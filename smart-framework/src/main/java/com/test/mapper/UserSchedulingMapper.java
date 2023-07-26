package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Days;
import com.test.domain.entity.UserScheduling;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 用户和排班的关联表(UserScheduling)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:47:45
 */
@Mapper
public interface UserSchedulingMapper extends BaseMapper<UserScheduling> {

    List<UserScheduling> selectData(Long id, String startDate, String endDate);

    List<Days> selectDays(Long id, String startDate, String endDate);
}
