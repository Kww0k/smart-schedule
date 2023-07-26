package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Days;
import com.test.domain.entity.StoreWorked;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 日排班表(Days)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:44:59
 */
@Mapper
public interface DaysMapper extends BaseMapper<Days> {

    List<StoreWorked> selectWorkedTime(@Param("userId") Long userId, @Param("mondayDate") String mondayDate, @Param("end") String end);

    List<Days> selectOneSchedule(Long id);

    List<Days> selectDaysList(@Param("id") Long id, @Param("mondayDate") String mondayDate, @Param("sundayDate") String sundayDate);

    List<Days> selectDays(@Param("id") Long id, @Param("date") String data);

    @Delete("delete from scheduling_days where id = #{schedulingId}")
    void realDelete(Long schedulingId);
}
