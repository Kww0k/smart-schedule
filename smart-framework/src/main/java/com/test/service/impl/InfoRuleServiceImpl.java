package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.entity.InfoRule;
import com.test.domain.entity.Rule;
import com.test.domain.vo.RuleVo;
import com.test.mapper.InfoRuleMapper;
import com.test.mapper.RuleMapper;
import com.test.service.InfoRuleService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 门店信息与自定义规则的关联表(InfoRule)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:45:50
 */
@Service("infoRuleService")
public class InfoRuleServiceImpl extends ServiceImpl<InfoRuleMapper, InfoRule> implements InfoRuleService {

    @Autowired
    RuleMapper ruleMapper;

    @Override
    public ResponseResult findByStoreId(Long id) {
        LambdaQueryWrapper<InfoRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoRule::getInfoId, id);
        InfoRule infoRule = baseMapper.selectOne(wrapper);
        Rule rule = ruleMapper.selectById(infoRule.getRuleId());
        RuleVo ruleVo = BeanCopyUtils.copyBean(rule, RuleVo.class);
        return ResponseResult.okResult(ruleVo);
    }
}
