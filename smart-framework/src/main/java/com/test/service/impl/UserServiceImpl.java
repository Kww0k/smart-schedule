package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddUserDto;
import com.test.domain.dto.NewPasswordDto;
import com.test.domain.dto.UpdateUserDto;
import com.test.domain.entity.*;
import com.test.domain.vo.AllStoreVo;
import com.test.domain.vo.PageInfoVo;
import com.test.domain.vo.RoleMenu;
import com.test.domain.vo.UserInfoVo;
import com.test.enums.AppHttpCodeEnum;
import com.test.mapper.*;
import com.test.service.UserService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;

import static com.test.constants.SystemConstants.*;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:47:12
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private InfoUserMapper infoUserMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private InfoMapper infoMapper;
    @Autowired
    private RoleMapper roleMapper;

    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public ResponseResult userList(Integer pageNum, Integer pageSize, String name, String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), User::getName, name);
        wrapper.like(StringUtils.hasText(email), User::getEmail, email);
        Long count = baseMapper.selectCount(wrapper);
        Page<User> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<UserInfoVo> userInfoVos = BeanCopyUtils.copyBeanList(page.getRecords(), UserInfoVo.class);
        for (UserInfoVo userInfoVo: userInfoVos) {
            String store = baseMapper.selectStore(userInfoVo.getId());
            userInfoVo.setStore(store);
            String role = baseMapper.selectRole(userInfoVo.getId());
            userInfoVo.setRole(role);
        }
        PageInfoVo pageInfoVo = new PageInfoVo(count, userInfoVos);
        return ResponseResult.okResult(pageInfoVo);
    }

    @Override
    @Transactional
    public ResponseResult updateUserInfo(UpdateUserDto updateUserDto) {
        User user = BeanCopyUtils.copyBean(updateUserDto, User.class);
        updateById(user);
        if (updateUserDto.getStore() != null) {
            LambdaQueryWrapper<Info> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(Info::getName, updateUserDto.getStore());
            Info info = infoMapper.selectOne(wrapper2);
            LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InfoUser::getUserId, user.getId());
            infoUserMapper.delete(wrapper);
            infoUserMapper.insert(new InfoUser(info.getId(), user.getId()));
        }
        if (updateUserDto.getRole() != null) {
            LambdaQueryWrapper<Role> wrapper3 = new LambdaQueryWrapper<>();
            wrapper3.eq(Role::getRoleName, updateUserDto.getRole());
            Role role = roleMapper.selectOne(wrapper3);
            LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserRole::getUserId, user.getId());
            userRoleMapper.delete(lambdaQueryWrapper);
            userRoleMapper.insert(new UserRole(user.getId(), role.getId()));
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult findByStoreId(Long id) {
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getInfoId, id);
        List<InfoUser> infoUsers = infoUserMapper.selectList(wrapper);
        List<UserInfoVo> userList = new ArrayList<>();
        for (InfoUser infoUser : infoUsers) {
            User user = baseMapper.selectById(infoUser.getUserId());
            UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
            userList.add(userInfoVo);
        }
        return ResponseResult.okResult(userList);
    }

    @Override
    @Transactional
    public ResponseResult deleteUserById(Long id) {
        int i = baseMapper.deleteById(id);
        if (i == 0) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_USER_ID);
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getUserId, id);
        infoUserMapper.delete(wrapper);
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, id);
        userRoleMapper.delete(queryWrapper);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult addUser(AddUserDto addUserDto) {
        if (addUserDto.getRole() == 1L) return ResponseResult.errorResult(AppHttpCodeEnum.CANT_BE_ADMIN);
        User user = BeanCopyUtils.copyBean(addUserDto, User.class);
        if (!StringUtils.hasText(user.getName()) ||
                !StringUtils.hasText(user.getEmail()) ||
                !StringUtils.hasText(user.getPassword()) ||
                !StringUtils.hasText(user.getPhoneNumber()))
            return ResponseResult.errorResult(AppHttpCodeEnum.NULL_PARAM);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, user.getEmail());
        User user1 = baseMapper.selectOne(wrapper);
        if (!Objects.isNull(user1)) return ResponseResult.errorResult(AppHttpCodeEnum.HAVE_USER);
        String encode = bCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encode);
        boolean save = save(user);
        if (!save) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_INSERT_USER);
        if (addUserDto.getStore() != null && addUserDto.getStore() >= 1L)
            infoUserMapper.insert(new InfoUser(addUserDto.getStore(), user.getId()));
        if (addUserDto.getRole() != null && addUserDto.getRole() >= 2L)
            userRoleMapper.insert(new UserRole(user.getId(), addUserDto.getRole()));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteBatch(List<Long> ids) {
        int i = baseMapper.deleteBatchIds(ids);
        if (i == 0) return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_USER_ID);
        for (Long id : ids) {
            LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InfoUser::getUserId, id);
            infoUserMapper.delete(wrapper);
            LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserRole::getUserId, id);
            userRoleMapper.delete(queryWrapper);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserById(Long id) {
        User user = baseMapper.selectById(id);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    @Transactional
    public ResponseResult password(NewPasswordDto newPasswordDto) {
        User user = baseMapper.selectById(newPasswordDto.getId());
        if (bCryptPasswordEncoder().matches(newPasswordDto.getPassword(), user.getPassword()) &&
                (Objects.equals(newPasswordDto.getNewPassword(), newPasswordDto.getConfirmPassword()))) {
            user.setPassword(bCryptPasswordEncoder().encode(newPasswordDto.getNewPassword()));
            updateById(user);
            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public ResponseResult store() {
        List<Info> infos = infoMapper.selectList(null);
        List<AllStoreVo> allStoreVos = BeanCopyUtils.copyBeanList(infos, AllStoreVo.class);
        return ResponseResult.okResult(allStoreVos);
    }

    @Override
    public ResponseResult role() {
        List<Role> roles = roleMapper.selectList(null);
        List<Role> roleList = roles.stream()
                .filter(role -> role.getId() != 1L)
                .toList();
        List<RoleMenu> roleMenus = BeanCopyUtils.copyBeanList(roleList, RoleMenu.class);
        return ResponseResult.okResult(roleMenus);
    }

    @Override
    public ResponseResult members() {
        List<Info> infos = infoMapper.selectList(null);
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<Long> longs = new ArrayList<>();
        for (Info info : infos) {
            list.add(info.getName());
            LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InfoUser::getInfoId, info.getId());
            Long count = infoUserMapper.selectCount(wrapper);
            longs.add(count);
        }
        map.put("x", list);
        map.put("y", longs);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult number() {
        Long count = baseMapper.selectCount(null);
        count = count - 1;
        return ResponseResult.okResult(count);
    }

}
