package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddInfoDto;
import com.test.domain.dto.UpdateInfoDto;
import com.test.domain.entity.Info;

import java.util.List;


/**
 * 门店信息(Info)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:45:36
 */
public interface InfoService extends IService<Info> {

    ResponseResult getStoreList(String name, String address);

    ResponseResult updateStoreInfo(UpdateInfoDto updateInfoDto);

    ResponseResult addStore(AddInfoDto addInfoDto);

    ResponseResult deleteStore(Long id);

    ResponseResult getStoreInfo(Long id);

    ResponseResult deleteBatch(List<Long> ids);

    ResponseResult number();
}
