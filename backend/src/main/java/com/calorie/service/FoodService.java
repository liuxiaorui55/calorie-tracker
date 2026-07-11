package com.calorie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calorie.entity.Food;
import com.calorie.entity.MealRecord;
import com.calorie.mapper.FoodMapper;
import com.calorie.mapper.MealRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 食物管理 Service
 */
@Service
public class FoodService extends ServiceImpl<FoodMapper, Food> {

    private final MealRecordMapper mealRecordMapper;

    public FoodService(MealRecordMapper mealRecordMapper) {
        this.mealRecordMapper = mealRecordMapper;
    }

    /**
     * 按关键词 + 分类查询食物列表
     * @param keyword  名称关键词（可选）
     * @param category 分类（可选）
     */
    public List<Food> search(String keyword, String category) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        // 关键词模糊搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Food::getName, keyword);
        }
        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(Food::getCategory, category);
        }
        // 按分类 + 名称排序
        wrapper.orderByAsc(Food::getCategory, Food::getName);
        return list(wrapper);
    }

    /**
     * 删除食物，如果有关联的餐食记录则抛出异常
     */
    public boolean removeById(Long id) {
        LambdaQueryWrapper<MealRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MealRecord::getFoodId, id);
        Long count = mealRecordMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new RuntimeException(
                    String.format("该食物已被 %d 条餐食记录引用，无法删除。请先删除相关餐食记录。", count));
        }
        return super.removeById(id);
    }
}
