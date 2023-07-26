package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.entity.InfoRule;


/**
 * 门店信息与自定义规则的关联表(InfoRule)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:45:50
 */
public interface InfoRuleService extends IService<InfoRule> {


    ResponseResult findByStoreId(Long id);
}
