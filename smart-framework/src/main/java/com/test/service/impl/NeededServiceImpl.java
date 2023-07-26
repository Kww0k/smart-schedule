package com.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.entity.Needed;
import com.test.mapper.NeededMapper;
import com.test.service.NeededService;
import org.springframework.stereotype.Service;

/**
 * 记录每个时段还需要多少员工的表(Needed)表服务实现类
 *
 * @author makejava
 * @since 2023-01-19 14:18:05
 */
@Service("neededService")
public class NeededServiceImpl extends ServiceImpl<NeededMapper, Needed> implements NeededService {

}
