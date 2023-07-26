package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Rule;
import org.apache.ibatis.annotations.Mapper;


/**
 * 自定义规则表(Rule)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:46:08
 */
@Mapper
public interface RuleMapper extends BaseMapper<Rule> {

    Rule selectByStoreId(Long storeId);
}
