package com.calorie.common;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result 统一响应测试
 */
class ResultTest {

    @Test
    void ok_shouldReturnSuccessResponse() {
        Map<String, Object> result = Result.ok("test data");

        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertEquals("test data", result.get("data"));
    }

    @Test
    void ok_withNullData_shouldReturnSuccessResponse() {
        Map<String, Object> result = Result.ok(null);

        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertNull(result.get("data"));
    }

    @Test
    void error_shouldReturnErrorResponse() {
        Map<String, Object> result = Result.error("something went wrong");

        assertEquals(500, result.get("code"));
        assertEquals("something went wrong", result.get("message"));
        assertNull(result.get("data"));
    }

    @Test
    void error_withCustomCode_shouldReturnErrorWithCode() {
        Map<String, Object> result = Result.error(400, "bad request");

        assertEquals(400, result.get("code"));
        assertEquals("bad request", result.get("message"));
        assertNull(result.get("data"));
    }
}
