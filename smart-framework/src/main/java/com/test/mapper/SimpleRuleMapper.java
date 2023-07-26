package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.SimpleRule;
import org.apache.ibatis.annotations.Mapper;


/**
 * 固定规则表(SimpleRule)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:46:42
 */
@Mapper
public interface SimpleRuleMapper extends BaseMapper<SimpleRule> {

}
