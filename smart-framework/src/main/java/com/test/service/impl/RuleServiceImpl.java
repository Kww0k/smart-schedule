package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddRuleDto;
import com.test.domain.dto.UpdateRuleDto;
import com.test.domain.entity.Info;
import com.test.domain.entity.InfoRule;
import com.test.domain.entity.Rule;
import com.test.domain.vo.AllStoreVo;
import com.test.domain.vo.RuleVo;
import com.test.enums.AppHttpCodeEnum;
import com.test.mapper.InfoMapper;
import com.test.mapper.InfoRuleMapper;
import com.test.mapper.RuleMapper;
import com.test.service.InfoService;
import com.test.service.RuleService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义规则表(Rule)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:46:08
 */
@Service("ruleService")
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements RuleService {

    @Autowired
    private InfoRuleMapper infoRuleMapper;
    @Autowired
    private InfoMapper infoMapper;

    @Override
    public ResponseResult getRuleList() {
        List<Rule> rules = baseMapper.selectList(null);
        List<RuleVo> ruleVos = BeanCopyUtils.copyBeanList(rules, RuleVo.class);
        for (RuleVo ruleVo: ruleVos) {
            LambdaQueryWrapper<InfoRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InfoRule::getRuleId, ruleVo.getId());
            InfoRule infoRule = infoRuleMapper.selectOne(wrapper);
            Info info = infoMapper.selectById(infoRule.getInfoId());
            ruleVo.setStore(info.getName());
        }
        return ResponseResult.okResult(ruleVos);
    }

    @Override
    public ResponseResult getRuleById(Long id) {
        Rule rule = baseMapper.selectById(id);
        if (rule == null) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_RULE_ID);
        RuleVo ruleVo = BeanCopyUtils.copyBean(rule, RuleVo.class);
        return ResponseResult.okResult(ruleVo);
    }

    @Override
    public ResponseResult addRule(AddRuleDto addRuleDto) {
        LambdaQueryWrapper<InfoRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoRule::getInfoId, addRuleDto.getStore());
        InfoRule infoRule = infoRuleMapper.selectOne(wrapper);
        if (infoRule != null) return ResponseResult.errorResult(AppHttpCodeEnum.HAVE_RULE);
        Rule rule = BeanCopyUtils.copyBean(addRuleDto, Rule.class);
        save(rule);
        infoRuleMapper.insert(new InfoRule(addRuleDto.getStore(), rule.getId()));
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult deleteRuleById(Long id) {
        int i = baseMapper.deleteById(id);
        if (i == 0) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_RULE_ID);
        LambdaQueryWrapper<InfoRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoRule::getRuleId, id);
        infoRuleMapper.delete(wrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateRuleById(UpdateRuleDto updateRuleDto) {
        Rule rule = BeanCopyUtils.copyBean(updateRuleDto, Rule.class);
        updateById(rule);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult findAllStore() {
        List<Info> infos = infoMapper.selectList(null);
        List<AllStoreVo> allStoreVos = BeanCopyUtils.copyBeanList(infos, AllStoreVo.class);
        return ResponseResult.okResult(allStoreVos);
    }




}
