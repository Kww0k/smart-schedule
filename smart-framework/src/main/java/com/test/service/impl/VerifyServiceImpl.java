package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.test.domain.ResponseResult;
import com.test.domain.dto.RegisterDto;
import com.test.domain.entity.*;
import com.test.domain.vo.AdminLoginVO;
import com.test.domain.vo.MenuVo;
import com.test.domain.vo.RouterVo;
import com.test.domain.vo.UserInfoVo;
import com.test.mapper.InfoMapper;
import com.test.mapper.InfoUserMapper;
import com.test.mapper.UserMapper;
import com.test.service.MenuService;
import com.test.service.VerifyService;
import com.test.utils.BeanCopyUtils;
import com.test.utils.JwtUtil;
import com.test.utils.RedisCache;
import com.test.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.test.constants.SystemConstants.ADMIN_LOGIN;
import static com.test.constants.SystemConstants.VERIFY;

@Service("verifyService")
public class VerifyServiceImpl implements VerifyService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MenuService menuService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private InfoMapper infoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private InfoUserMapper infoUserMapper;
    @Autowired
    private RedisCache redisCache;
    @Value("${spring.mail.username}")
    String from;

    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public ResponseResult getVerify(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("【惠博云通】");
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        stringRedisTemplate.opsForValue().set(VERIFY + email, code + "", 5, TimeUnit.MINUTES);
        message.setText("您的注册验证码为: " + code + "(有效期为5分钟)，请勿泄漏给他人，如非本人操作，请忽略此信息。");
        message.setTo(email);
        message.setFrom(from);
        javaMailSender.send(message);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult doRegister(RegisterDto registerDto) {
        User user;
        if (registerDto.getEmail() != null) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, registerDto.getEmail());
            user = userMapper.selectOne(wrapper);
        } else
            return ResponseResult.errorResult(404, "邮箱不能为空");
        if (user != null)
            return ResponseResult.errorResult(403, "邮箱已被注册");
        if (registerDto.getName() != null) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getName, registerDto.getName());
            User user1 = userMapper.selectOne(wrapper);
            if (user1 != null) return ResponseResult.errorResult(403, "用户名已被注册");
        }
        if (StringUtils.hasText(registerDto.getCode())) {
            String s = stringRedisTemplate.opsForValue().get(VERIFY + registerDto.getEmail());
            if (s == null) return ResponseResult.errorResult(404, "验证码不存在");
            if (!s.equals(registerDto.getCode()))
                return ResponseResult.errorResult(403, "验证码错误");
        } else ResponseResult.errorResult(404, "验证码不能为空");
        Info info;
        if (registerDto.getStoreCode() != null) {
            LambdaQueryWrapper<Info> infoWrapper = new LambdaQueryWrapper<>();
            infoWrapper.eq(Info::getCode, registerDto.getStoreCode());
            info = infoMapper.selectOne(infoWrapper);
            if (info == null)
                return ResponseResult.errorResult(404, "门店不存在");
        } else
            return ResponseResult.errorResult(404, "门店码不能为空");
        if (!StringUtils.hasText(registerDto.getName()) || !StringUtils.hasText(registerDto.getPassword()))
            return ResponseResult.errorResult(404, "用户名或密码不能为空");
        User addUser = new User();
        addUser.setName(registerDto.getName());
        addUser.setEmail(registerDto.getEmail());
        addUser.setPassword(bCryptPasswordEncoder().encode(registerDto.getPassword()));
        userMapper.insert(addUser);
        infoUserMapper.insert(new InfoUser(info.getId(), addUser.getId()));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate))
            return ResponseResult.errorResult(400, "用户名或密码错误");
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String id = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(id);
        redisCache.setCacheObject(ADMIN_LOGIN + id, loginUser);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        AdminLoginVO adminLoginVO = new AdminLoginVO(jwt, userInfoVo);
        return ResponseResult.okResult(adminLoginVO);
    }

    @Override
    public ResponseResult logout() {
        Long id = SecurityUtils.getUserId();
        redisCache.deleteObject(ADMIN_LOGIN + id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getRouter() {
        Long userId = SecurityUtils.getUserId();
        List<MenuVo> menus = menuService.selectTree(userId);
        return ResponseResult.okResult(new RouterVo(menus));
    }
}
