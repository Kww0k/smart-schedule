package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Files;

import java.util.List;


/**
 * (File)表服务接口
 *
 * @author makejava
 * @since 2023-01-12 14:06:20
 */
public interface FileService extends IService<Files> {

    ResponseResult fileList(Integer pageNum, Integer pageSize, String name);

    ResponseResult deleteFileById(Long id);

    ResponseResult deleteBatch(List<Long> ids);

    ResponseResult updateFileInfo(Files files);
}
