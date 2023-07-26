package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.entity.AiData;


/**
 * ai预测数据表(AiData)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:51:03
 */
public interface AiDataService extends IService<AiData> {

    ResponseResult getAiDataByDate(String date);

    ResponseResult getAiData(String date);
}
