package com.test.service;

import com.test.domain.ResponseResult;
import com.test.domain.dto.RegisterDto;
import com.test.domain.entity.User;

public interface VerifyService {
    ResponseResult getVerify(String email);

    ResponseResult doRegister(RegisterDto registerDto);

    ResponseResult login(User user);

    ResponseResult logout();

    ResponseResult getRouter();
}
