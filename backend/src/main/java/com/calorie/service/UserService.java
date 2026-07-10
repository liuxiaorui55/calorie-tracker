package com.calorie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calorie.entity.User;
import com.calorie.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户 Service
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    /**
     * 注册
     */
    public User register(String username, String password, String gender, Integer age) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (count(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setGender(gender);
        user.setAge(age);
        save(user);
        return user;
    }

    /**
     * 登录 — 返回带 token 的用户信息
     */
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getPassword, password);
        User user = getOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 生成令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setToken(token);
        updateById(user);
        return user;
    }

    /**
     * 根据 token 查找用户
     */
    public User findByToken(String token) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getToken, token);
        return getOne(wrapper);
    }

    /**
     * 登出
     */
    public void logout(Long userId) {
        User user = getById(userId);
        if (user != null) {
            user.setToken(null);
            updateById(user);
        }
    }
}
