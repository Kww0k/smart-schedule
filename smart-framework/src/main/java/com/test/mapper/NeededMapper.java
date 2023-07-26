package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.Needed;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 记录每个时段还需要多少员工的表(Needed)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-19 14:18:04
 */
@Mapper
public interface NeededMapper extends BaseMapper<Needed> {

    List<Needed> selectDayNeeded(Long id, String date);

    List<Needed> selectNeeded(Long storeId, String date, double time, double workTime);

    @Delete("delete from scheduling_needed where id = #{neededId}")
    void realDelete(Long neededId);
}
