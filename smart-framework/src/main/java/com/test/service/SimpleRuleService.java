package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.entity.SimpleRule;


/**
 * 固定规则表(SimpleRule)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:46:42
 */
public interface SimpleRuleService extends IService<SimpleRule> {

    ResponseResult getSimpleRule();

}
