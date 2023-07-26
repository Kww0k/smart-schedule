package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Dict;


/**
 * (Dict)表服务接口
 *
 * @author makejava
 * @since 2022-12-18 16:51:37
 */
public interface DictService extends IService<Dict> {

    ResponseResult getIconList();
}
