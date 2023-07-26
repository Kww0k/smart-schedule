package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.InfoNeeded;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * (InfoNeeded)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-19 14:14:42
 */
@Mapper
public interface InfoNeededMapper extends BaseMapper<InfoNeeded> {

    List<Long> selectData(Long id, String startDate, String endDate);
}
