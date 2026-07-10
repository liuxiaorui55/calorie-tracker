package com.calorie.controller;

import com.calorie.entity.Food;
import com.calorie.service.FoodService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 食物管理 Controller
 */
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    /** 食物列表（支持 keyword + category 筛选） */
    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String category) {
        List<Food> list = foodService.search(keyword, category);
        return ok(list);
    }

    /** 单个食物详情 */
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Food food = foodService.getById(id);
        return food != null ? ok(food) : error("食物不存在");
    }

    /** 新增食物 */
    @PostMapping
    public Map<String, Object> add(@RequestBody Food food) {
        foodService.save(food);
        return ok(food);
    }

    /** 更新食物 */
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Food food) {
        food.setId(id);
        foodService.updateById(food);
        return ok(food);
    }

    /** 删除食物 */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        foodService.removeById(id);
        return ok(null);
    }

    /** 统一成功响应 */
    private Map<String, Object> ok(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    /** 统一错误响应 */
    private Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", message);
        result.put("data", null);
        return result;
    }
}
