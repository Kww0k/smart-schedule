package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddRuleDto;
import com.test.domain.dto.UpdateRuleDto;
import com.test.domain.entity.Rule;


/**
 * 自定义规则表(Rule)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:46:08
 */
public interface RuleService extends IService<Rule> {

    ResponseResult getRuleList();

    ResponseResult getRuleById(Long id);

    ResponseResult addRule(AddRuleDto addRuleDto);

    ResponseResult deleteRuleById(Long id);

    ResponseResult updateRuleById(UpdateRuleDto updateRuleDto);

    ResponseResult findAllStore();

}
