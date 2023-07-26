package com.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.entity.UserRest;
import com.test.mapper.UserRestMapper;
import com.test.service.UserRestService;
import org.springframework.stereotype.Service;

/**
 * 用户和申请信息的关联表(UserRest)表服务实现类
 *
 * @author makejava
 * @since 2023-03-14 16:07:14
 */
@Service("userRestService")
public class UserRestServiceImpl extends ServiceImpl<UserRestMapper, UserRest> implements UserRestService {

}
