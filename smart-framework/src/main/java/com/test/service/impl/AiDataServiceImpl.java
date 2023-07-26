package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.entity.AiData;
import com.test.domain.entity.Info;
import com.test.domain.entity.InfoData;
import com.test.domain.vo.AiDataVo;
import com.test.mapper.AiDataMapper;
import com.test.mapper.InfoDataMapper;
import com.test.mapper.InfoMapper;
import com.test.service.AiDataService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ai预测数据表(AiData)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:58:07
 */
@Service("aiDataService")
public class AiDataServiceImpl extends ServiceImpl<AiDataMapper, AiData> implements AiDataService {

    @Autowired
    InfoDataMapper infoDataMapper;
    @Autowired
    InfoMapper infoMapper;

    @Override
    public ResponseResult getAiDataByDate(String date) {
        LambdaQueryWrapper<AiData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiData::getDate, date);
        AiData aiData = baseMapper.selectOne(wrapper);
        return ResponseResult.okResult(aiData);
    }

    @Override
    public ResponseResult getAiData(String date) {
        LambdaQueryWrapper<AiData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiData::getDate, date);
        List<AiData> aiData = baseMapper.selectList(wrapper);
        List<AiDataVo> aiDataVos = BeanCopyUtils.copyBeanList(aiData, AiDataVo.class);
        for (AiDataVo aiDataVo : aiDataVos) {
            LambdaQueryWrapper<InfoData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InfoData::getDataId, aiDataVo.getId());
            InfoData infoData = infoDataMapper.selectOne(queryWrapper);
            Info info = infoMapper.selectById(infoData.getInfoId());
            aiDataVo.setName(info.getName());
        }
        return ResponseResult.okResult(aiDataVos);
    }


}
