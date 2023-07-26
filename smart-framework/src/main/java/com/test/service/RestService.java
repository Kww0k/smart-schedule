package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.RestDto;
import com.test.domain.entity.Rest;


/**
 * 申请休息表(Rest)表服务接口
 *
 * @author makejava
 * @since 2023-03-14 16:07:32
 */
public interface RestService extends IService<Rest> {

    ResponseResult getApply(Integer pageNum, Integer pageSize);

    ResponseResult allowApply(RestDto rest);
}
