package com.test.utils;

import com.test.domain.entity.LoginUser;
import com.test.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.test.constants.SystemConstants.ADMIN_ID;


/**
 * @Author 三更  B站： https://space.bilibili.com/663528522
 */
public class SecurityUtils {

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {
        if (getAuthentication().getPrincipal() != null)
            return (LoginUser) getAuthentication().getPrincipal();
        else
            return new LoginUser(new User());
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Boolean isAdmin() {
        Long id = getLoginUser().getUser().getId();
        return id != null && id.equals(ADMIN_ID);
    }

    public static Long getUserId() {
        return getLoginUser().getUser().getId();
    }
}