package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.ResponseResult;
import com.test.domain.entity.AiData;
import org.apache.ibatis.annotations.Mapper;


/**
 * ai预测数据表(AiData)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:51:03
 */
@Mapper
public interface AiDataMapper extends BaseMapper<AiData> {

    AiData selectData(Long storeId, String end);

}
