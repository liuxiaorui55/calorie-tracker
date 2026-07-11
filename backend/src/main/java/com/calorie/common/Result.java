package com.calorie.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一 API 响应封装
 */
public final class Result {

    private Result() {
        // 工具类，禁止实例化
    }

    /** 统一成功响应 */
    public static Map<String, Object> ok(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    /** 统一错误响应 */
    public static Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", message);
        result.put("data", null);
        return result;
    }

    /** 带错误码的错误响应 */
    public static Map<String, Object> error(int code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);
        return result;
    }
}
