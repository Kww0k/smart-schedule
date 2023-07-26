package com.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.entity.SimpleRule;
import com.test.mapper.SimpleRuleMapper;
import com.test.service.SimpleRuleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.test.constants.SystemConstants.SIMPLE_RULE_ID;

/**
 * 固定规则表(SimpleRule)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:46:42
 */
@Service("simpleRuleService")
public class SimpleRuleServiceImpl extends ServiceImpl<SimpleRuleMapper, SimpleRule> implements SimpleRuleService {

    @Override
    public ResponseResult getSimpleRule() {
        SimpleRule simpleRule = baseMapper.selectById(SIMPLE_RULE_ID);
        List<SimpleRule> list = new ArrayList<>();
        list.add(simpleRule);
        return ResponseResult.okResult(list);
    }

}
