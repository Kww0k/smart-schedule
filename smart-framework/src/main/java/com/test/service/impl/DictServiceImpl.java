package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.constants.SystemConstants;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Dict;
import com.test.mapper.DictMapper;
import com.test.service.DictService;
import org.springframework.stereotype.Service;

/**
 * (Dict)表服务实现类
 *
 * @author makejava
 * @since 2022-12-18 16:51:37
 */
@Service("dictService")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public ResponseResult getIconList() {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getTpye, SystemConstants.DICT_TYPE_ICON);
        return ResponseResult.okResult(baseMapper.selectList(wrapper));
    }
}
