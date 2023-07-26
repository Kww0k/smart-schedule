package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddUserDto;
import com.test.domain.dto.NewPasswordDto;
import com.test.domain.dto.UpdateUserDto;
import com.test.domain.entity.User;

import java.util.List;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:47:12
 */
public interface UserService extends IService<User> {

    ResponseResult userList(Integer pageNum, Integer pageSize, String name, String email);

    ResponseResult updateUserInfo(UpdateUserDto updateUserDto);

    ResponseResult findByStoreId(Long id);

    ResponseResult deleteUserById(Long id);

    ResponseResult addUser(AddUserDto addUserDto);

    ResponseResult deleteBatch(List<Long> ids);

    ResponseResult getUserById(Long id);

    ResponseResult password(NewPasswordDto newPasswordDto);

    ResponseResult store();

    ResponseResult role();

    ResponseResult members();

    ResponseResult number();
}
