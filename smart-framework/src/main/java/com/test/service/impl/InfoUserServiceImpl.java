package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.entity.InfoUser;
import com.test.mapper.InfoUserMapper;
import com.test.service.InfoUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门店与用户的关联表(InfoUser)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:44:30
 */
@Service("infoUserService")
public class InfoUserServiceImpl extends ServiceImpl<InfoUserMapper, InfoUser> implements InfoUserService {


}
