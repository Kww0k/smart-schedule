package com.test.handler.security;

import com.alibaba.fastjson.JSON;
import com.test.domain.ResponseResult;
import com.test.enums.AppHttpCodeEnum;
import com.test.utils.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        authException.printStackTrace();
        ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_ERROE);
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}
