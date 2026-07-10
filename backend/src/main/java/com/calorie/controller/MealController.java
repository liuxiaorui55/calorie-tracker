package com.calorie.controller;

import com.calorie.entity.MealRecord;
import com.calorie.service.MealService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 餐食管理 Controller
 */
@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    /** 某天所有餐食记录 */
    @GetMapping
    public Map<String, Object> list(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<MealRecord> records = mealService.getByDate(localDate);
        return ok(records);
    }

    /** 新增一条餐食记录 */
    @PostMapping
    public Map<String, Object> add(@RequestBody MealRecord record) {
        try {
            MealRecord saved = mealService.addRecord(record);
            return ok(saved);
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    /** 删除一条餐食记录 */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        mealService.removeById(id);
        return ok(null);
    }

    /** 某天饮食汇总 */
    @GetMapping("/summary")
    public Map<String, Object> summary(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        Map<String, Object> summary = mealService.getSummary(localDate);
        return ok(summary);
    }

    /** 某天饮食建议 */
    @GetMapping("/suggestion")
    public Map<String, Object> suggestion(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        Map<String, Object> suggestion = mealService.getSuggestion(localDate);
        return ok(suggestion);
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
