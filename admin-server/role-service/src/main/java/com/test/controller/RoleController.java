package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddRoleDto;
import com.test.domain.dto.AddUserDto;
import com.test.domain.dto.UpdateRoleDto;
import com.test.domain.dto.UpdateUserDto;
import com.test.service.RoleMenuService;
import com.test.service.RoleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@Api(tags = "角色接口", description = "有关角色的一系列操作")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("分页查询所有角色信息")
    @GetMapping("/page")
    @SystemLog(businessName = "分页查询所有角色信息")
    public ResponseResult roleList(@ApiParam("页数") Integer pageNum,
                                   @ApiParam("一页有几个") Integer pageSize,
                                   @ApiParam("角色名称") String roleName) {
        return roleService.roleList(pageNum, pageSize, roleName);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 404, message = "没有这个角色")
    })
    @ApiOperation("根据id删除角色信息")
    @DeleteMapping("/deleteById")
    @SystemLog(businessName = "根据id删除角色信息")
    public ResponseResult deleteRoleById(@ApiParam("id")@RequestParam Long id) {
        return roleService.deleteRoleById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("根据id批量删除角色信息")
    @PostMapping("/deleteBatch")
    @SystemLog(businessName = "根据id批量删除角色信息")
    public ResponseResult deleteBatch(@ApiParam("ids") @RequestBody List<Long> ids) {
        return roleService.deleteBatch(ids);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @ApiOperation("更新角色信息")
    @PostMapping ("/updateRole")
    @SystemLog(businessName = "更新角色信息")
    public ResponseResult updateRoleInfo(@ApiParam("更新后的信息") @RequestBody UpdateRoleDto updateRoleDto) {
        return roleService.updateRoleInfo(updateRoleDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "增加成功"),
            @ApiResponse(code = 400, message = "缺少参数"),
            @ApiResponse(code = 404, message = "插入时出错"),
            @ApiResponse(code = 405, message = "角色已被注册")
    })
    @ApiOperation("增加一个角色")
    @PutMapping("/addRole")
    @SystemLog(businessName = "增加一个角色")
    public ResponseResult addRole(@ApiParam("新增的信息") @RequestBody AddRoleDto addRoleDto) {
        return roleService.addRole(addRoleDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("绑定当前角色的菜单信息")
    @PostMapping("/menu/{roleId}")
    @SystemLog(businessName = "绑定当前角色的菜单信息")
    public ResponseResult menu(@ApiParam("角色id") @PathVariable("roleId") Long roleId,
                               @ApiParam("菜单的id数组") @RequestBody List<Long> menuIds) {
        return roleMenuService.menu(roleId, menuIds);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @ApiOperation("获取当前角色的菜单信息")
    @GetMapping("/roleMenu/{roleId}")
    @SystemLog(businessName = "获取当前角色的菜单信息")
    public ResponseResult getRoleMenu(@ApiParam("角色id") @PathVariable("roleId") Long roleId) {
        return roleMenuService.getRoleMenu(roleId);
    }

}
