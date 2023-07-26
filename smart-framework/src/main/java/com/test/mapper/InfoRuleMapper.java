package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.InfoRule;
import org.apache.ibatis.annotations.Mapper;


/**
 * 门店信息与自定义规则的关联表(InfoRule)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:45:50
 */
@Mapper
public interface InfoRuleMapper extends BaseMapper<InfoRule> {

}
