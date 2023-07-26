package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Files;
import com.test.domain.vo.FileInfoVo;
import com.test.domain.vo.PageFileVo;
import com.test.mapper.FileMapper;
import com.test.service.FileService;
import com.test.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * (File)表服务实现类
 *
 * @author makejava
 * @since 2023-01-12 14:06:20
 */
@Service("fileService")
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements FileService {

    @Override
    public ResponseResult fileList(Integer pageNum, Integer pageSize, String name) {
        LambdaQueryWrapper<Files> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Files::getName, name);
        Long count = baseMapper.selectCount(wrapper);
        Page<Files> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<FileInfoVo> fileInfoVos = BeanCopyUtils.copyBeanList(page.getRecords(), FileInfoVo.class);
        PageFileVo pageFileVo = new PageFileVo(count, fileInfoVos);
        return ResponseResult.okResult(pageFileVo);
    }

    @Override
    public ResponseResult deleteFileById(Long id) {
        baseMapper.deleteById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteBatch(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateFileInfo(Files files) {
        if (files.getStatus() == 1) {
            files.setStatus(0);
        }
        else {
            files.setStatus(1);
        }
        baseMapper.updateById(files);
        return ResponseResult.okResult();
    }
}
