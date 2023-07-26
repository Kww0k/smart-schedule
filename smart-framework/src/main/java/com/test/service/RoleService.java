package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddRoleDto;
import com.test.domain.dto.UpdateRoleDto;
import com.test.domain.entity.Role;

import java.util.List;


/**
 * 角色表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:46:58
 */
public interface RoleService extends IService<Role> {

    ResponseResult roleList(Integer pageNum, Integer pageSize, String roleName);


    ResponseResult deleteRoleById(Long id);

    ResponseResult deleteBatch(List<Long> ids);

    ResponseResult updateRoleInfo(UpdateRoleDto updateRoleDto);

    ResponseResult addRole(AddRoleDto addRoleDto);

    ResponseResult roleMember();
}
