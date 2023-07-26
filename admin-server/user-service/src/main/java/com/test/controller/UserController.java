package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddUserDto;
import com.test.domain.dto.NewPasswordDto;
import com.test.domain.dto.UpdateUserDto;
import com.test.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户信息", description = "对用户信息的一系列操作")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("分页查询所有员工信息")
    @GetMapping("/page")
    @SystemLog(businessName = "分页查询所有员工信息")
    public ResponseResult userList(@ApiParam("页数") Integer pageNum,
                                   @ApiParam("一页有几个") Integer pageSize,
                                   @ApiParam("用户名") String name,
                                   @ApiParam("邮箱") String email) {
        return userService.userList(pageNum, pageSize, name, email);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("根据门店id查找相应员工")
    @GetMapping("/findByStoreId")
    @SystemLog(businessName = "根据门店id查找相应员工")
    public ResponseResult findByStoreId(@ApiParam("id") @RequestParam Long id) {
        return userService.findByStoreId(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 404, message = "没有这个员工")
    })
    @ApiOperation("根据id删除员工信息")
    @DeleteMapping("/deleteById")
    @SystemLog(businessName = "根据id删除员工信息")
    public ResponseResult deleteById(@ApiParam("id") @RequestParam Long id) {
        return userService.deleteUserById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("根据id批量删除员工信息")
    @PostMapping("/deleteBatch")
    @SystemLog(businessName = "根据id批量删除员工信息")
    public ResponseResult deleteBatch(@ApiParam("ids") @RequestBody List<Long> ids) {
        return userService.deleteBatch(ids);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @ApiOperation("更新员工信息")
    @PostMapping ("/updateUser")
    @SystemLog(businessName = "更新员工信息")
    public ResponseResult updateUserInfo(@ApiParam("更新后的信息") @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUserInfo(updateUserDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "增加成功"),
            @ApiResponse(code = 400, message = "缺少参数"),
            @ApiResponse(code = 404, message = "插入时出错"),
            @ApiResponse(code = 405, message = "邮箱已被注册")
    })
    @ApiOperation("增加一个员工")
    @PutMapping("/addUser")
    @SystemLog(businessName = "增加一个员工")
    public ResponseResult addUser(@ApiParam("新增的信息") @RequestBody AddUserDto addUserDto) {
        return userService.addUser(addUserDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("根据id查询用户信息")
    @GetMapping("/getUserById/{id}")
    @SystemLog(businessName = "根据id查询用户信息")
    public ResponseResult getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "修改密码")
    })
    @ApiOperation("修改密码")
    @PostMapping("/password")
    @SystemLog(businessName = "修改密码")
    public ResponseResult password(@RequestBody NewPasswordDto newPasswordDto) {
        return userService.password(newPasswordDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "修改密码")
    })
    @ApiOperation("获取所有门店信息")
    @GetMapping("/store")
    @SystemLog(businessName = "获取所有门店信息")
    public ResponseResult store() {
        return userService.store();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "修改密码")
    })
    @ApiOperation("获取除管理员外的所有角色信息")
    @GetMapping("/role")
    @SystemLog(businessName = "获取除管理员外的所有角色信息")
    public ResponseResult role() {
        return userService.role();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "修改密码")
    })
    @ApiOperation("获取所有门店的员工数量")
    @GetMapping("/number")
    @SystemLog(businessName = "获取所有门店的员工数量")
    public ResponseResult number() {
        return userService.number();
    }
}
