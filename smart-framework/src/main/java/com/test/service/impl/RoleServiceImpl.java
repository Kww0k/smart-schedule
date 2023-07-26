package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.constants.SystemConstants;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddRoleDto;
import com.test.domain.dto.UpdateRoleDto;
import com.test.domain.entity.Role;
import com.test.domain.entity.User;
import com.test.domain.entity.UserRole;
import com.test.domain.vo.PageRoleVo;
import com.test.domain.vo.RoleInfoVo;
import com.test.enums.AppHttpCodeEnum;
import com.test.mapper.RoleMapper;
import com.test.mapper.UserRoleMapper;
import com.test.service.RoleService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("roleServiceImpl")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    UserRoleMapper userRoleMapper;

    @Override
    public ResponseResult roleList(Integer pageNum, Integer pageSize, String roleName) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName);
        Long count = baseMapper.selectCount(wrapper);
        Page<Role> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<RoleInfoVo> roleInfoVos = BeanCopyUtils.copyBeanList(page.getRecords(), RoleInfoVo.class);
        for (RoleInfoVo roleInfoVo : roleInfoVos) {
            LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserRole::getRoleId, roleInfoVo.getId());
            roleInfoVo.setCount(userRoleMapper.selectCount(queryWrapper));
        }
        PageRoleVo pageRoleVo = new PageRoleVo(count, roleInfoVos);
        return ResponseResult.okResult(pageRoleVo);
    }

    @Override
    public ResponseResult deleteRoleById(Long id) {
        int i = baseMapper.deleteById(id);
        if (i == 0) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_ROLE_ID);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleId, id);
        userRoleMapper.delete(wrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteBatch(List<Long> ids) {
        int i = baseMapper.deleteBatchIds(ids);
        if (i == 0) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_ROLE_ID);
        for (Long id : ids) {
            LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRole::getRoleId, id);
            userRoleMapper.delete(wrapper);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateRoleInfo(UpdateRoleDto updateRoleDto) {
        Role role = BeanCopyUtils.copyBean(updateRoleDto, Role.class);
        updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult addRole(AddRoleDto addRoleDto) {
        Role role = BeanCopyUtils.copyBean(addRoleDto, Role.class);
        if (!StringUtils.hasText(role.getRoleName())) return ResponseResult.errorResult(AppHttpCodeEnum.NULL_PARAM);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, role.getRoleName());
        Long count = baseMapper.selectCount(wrapper);
        if (count != 0) return ResponseResult.errorResult(AppHttpCodeEnum.HAVE_USER);
        save(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult roleMember() {
        List<Role> roles = list();
        List<Role> list = roles.stream().filter(role -> role.getId() != 1L).toList();
        Map<String, Object> map = new HashMap<>();
        List<String> lists = new ArrayList<>();
        List<Long> longs = new ArrayList<>();
        for (Role role : list) {
            lists.add(role.getRoleName());
            LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRole::getRoleId, role.getId());
            Long count = userRoleMapper.selectCount(wrapper);
            longs.add(count);
        }
        map.put("x", lists);
        map.put("y", longs);
        return ResponseResult.okResult(map);
    }


}
