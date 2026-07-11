package com.calorie.controller;

import com.calorie.common.Result;
import com.calorie.entity.User;
import com.calorie.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户 Controller
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (password.trim().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }

        try {
            String gender = (String) body.getOrDefault("gender", "");
            Integer age = body.get("age") != null ? ((Number) body.get("age")).intValue() : null;
            if (age != null && age <= 0) {
                return Result.error("年龄必须大于0");
            }
            User user = userService.register(username.trim(), password, gender, age);
            return Result.ok(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /** 登录 */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        try {
            User user = userService.login(username.trim(), password);
            // 返回时隐藏密码
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("gender", user.getGender());
            data.put("age", user.getAge());
            data.put("token", user.getToken());
            return Result.ok(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /** 登出 */
    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader("Authorization") String token) {
        User user = userService.findByToken(token);
        if (user != null) {
            userService.logout(user.getId());
        }
        return Result.ok(null);
    }

    /** 获取当前用户信息 */
    @GetMapping("/info")
    public Map<String, Object> info(@RequestHeader("Authorization") String token) {
        User user = userService.findByToken(token);
        if (user == null) {
            return Result.error("未登录");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("gender", user.getGender());
        data.put("age", user.getAge());
        return Result.ok(data);
    }
}
