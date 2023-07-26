package com.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.entity.InfoData;
import com.test.mapper.InfoDataMapper;
import com.test.service.InfoDataService;
import org.springframework.stereotype.Service;

/**
 * (InfoData)表服务实现类
 *
 * @author makejava
 * @since 2023-01-15 03:36:05
 */
@Service("infoDataService")
public class InfoDataServiceImpl extends ServiceImpl<InfoDataMapper, InfoData> implements InfoDataService {

}
