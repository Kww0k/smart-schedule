package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddInfoDto;
import com.test.domain.dto.UpdateInfoDto;
import com.test.domain.entity.Info;
import com.test.domain.vo.InfoVo;
import com.test.enums.AppHttpCodeEnum;
import com.test.mapper.InfoMapper;
import com.test.service.InfoService;
import com.test.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;

import static com.test.constants.SystemConstants.CODE_LEAST;
import static com.test.constants.SystemConstants.CODE_RANDOM;

/**
 * 门店信息(Info)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:45:36
 */
@Service("infoService")
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements InfoService {

    @Override
    public ResponseResult getStoreList(String name, String address) {
        LambdaQueryWrapper<Info> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Info::getName, name);
        wrapper.like(StringUtils.hasText(address), Info::getAddress, address);
        List<Info> infos = baseMapper.selectList(wrapper);
        List<InfoVo> infoVos = BeanCopyUtils.copyBeanList(infos, InfoVo.class);
        return ResponseResult.okResult(infoVos);
    }

    @Override
    public ResponseResult updateStoreInfo(UpdateInfoDto updateInfoDto) {
        Info info = BeanCopyUtils.copyBean(updateInfoDto, Info.class);
        updateById(info);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult addStore(AddInfoDto addInfoDto) {
        if (addInfoDto.getName() == null || addInfoDto.getAddress() == null || addInfoDto.getSize() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.LACK_PARAM);
        Info info = BeanCopyUtils.copyBean(addInfoDto, Info.class);
        Random random = new Random();
        int code = random.nextInt(CODE_RANDOM) + CODE_LEAST;
        info.setCode(code);
        save(info);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteStore(Long id) {
        int i = baseMapper.deleteById(id);
        if (i == 0) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_STORE_ID);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getStoreInfo(Long id) {
        Info info = baseMapper.selectById(id);
        if (info == null) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_STORE_ID);
        InfoVo infoVo = BeanCopyUtils.copyBean(info, InfoVo.class);
        return ResponseResult.okResult(infoVo);
    }

    @Override
    public ResponseResult deleteBatch(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult number() {
        Long count = baseMapper.selectCount(null);
        return ResponseResult.okResult(count);
    }
}
