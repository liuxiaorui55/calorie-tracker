package com.calorie.controller;

import com.calorie.common.Result;
import com.calorie.entity.Food;
import com.calorie.service.FoodService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        return Result.ok(list);
    }

    /** 单个食物详情 */
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Food food = foodService.getById(id);
        return food != null ? Result.ok(food) : Result.error("食物不存在");
    }

    /** 新增食物 */
    @PostMapping
    public Map<String, Object> add(@Valid @RequestBody Food food) {
        foodService.save(food);
        return Result.ok(food);
    }

    /** 更新食物 */
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody Food food) {
        food.setId(id);
        foodService.updateById(food);
        return Result.ok(food);
    }

    /** 删除食物 */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        try {
            foodService.removeById(id);
            return Result.ok(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
