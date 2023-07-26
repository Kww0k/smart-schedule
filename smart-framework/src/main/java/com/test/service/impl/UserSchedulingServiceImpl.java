package com.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.entity.UserScheduling;
import com.test.mapper.UserSchedulingMapper;
import com.test.service.UserSchedulingService;
import org.springframework.stereotype.Service;

/**
 * 用户和排班的关联表(UserScheduling)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:47:45
 */
@Service("userSchedulingService")
public class UserSchedulingServiceImpl extends ServiceImpl<UserSchedulingMapper, UserScheduling> implements UserSchedulingService {

}
