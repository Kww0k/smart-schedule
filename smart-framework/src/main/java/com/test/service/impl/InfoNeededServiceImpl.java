package com.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.entity.InfoNeeded;
import com.test.mapper.InfoNeededMapper;
import com.test.service.InfoNeededService;
import org.springframework.stereotype.Service;

/**
 * (InfoNeeded)表服务实现类
 *
 * @author makejava
 * @since 2023-01-19 14:14:44
 */
@Service("infoNeededService")
public class InfoNeededServiceImpl extends ServiceImpl<InfoNeededMapper, InfoNeeded> implements InfoNeededService {

}
