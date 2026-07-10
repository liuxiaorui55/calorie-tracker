package com.calorie.controller;

import com.calorie.entity.User;
import com.calorie.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        try {
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            String gender = (String) body.getOrDefault("gender", "");
            Integer age = body.get("age") != null ? ((Number) body.get("age")).intValue() : null;
            User user = userService.register(username, password, gender, age);
            return ok(user);
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    /** 登录 */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> body) {
        try {
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            User user = userService.login(username, password);
            // 返回时隐藏密码
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("gender", user.getGender());
            data.put("age", user.getAge());
            data.put("token", user.getToken());
            return ok(data);
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    /** 登出 */
    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader("Authorization") String token) {
        User user = userService.findByToken(token);
        if (user != null) {
            userService.logout(user.getId());
        }
        return ok(null);
    }

    /** 获取当前用户信息 */
    @GetMapping("/info")
    public Map<String, Object> info(@RequestHeader("Authorization") String token) {
        User user = userService.findByToken(token);
        if (user == null) {
            return error("未登录");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("gender", user.getGender());
        data.put("age", user.getAge());
        return ok(data);
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", message);
        result.put("data", null);
        return result;
    }
}
