package com.calorie.config;

import com.calorie.entity.User;
import com.calorie.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器 — 从 Authorization 头提取当前用户
 */
@Component
public class LoginFilter implements HandlerInterceptor {

    private final UserService userService;

    /** 当前请求用户（ThreadLocal） */
    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    public LoginFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            User user = userService.findByToken(token);
            if (user != null) {
                CURRENT_USER.set(user);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        CURRENT_USER.remove();
    }

    /** 获取当前登录用户 */
    public static User currentUser() {
        return CURRENT_USER.get();
    }
}
