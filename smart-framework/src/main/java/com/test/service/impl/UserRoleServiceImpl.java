package com.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.entity.UserRole;
import com.test.mapper.UserRoleMapper;
import com.test.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户与角色的关联表(UserRole)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:47:27
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
