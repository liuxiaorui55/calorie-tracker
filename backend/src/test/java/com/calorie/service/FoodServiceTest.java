package com.calorie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calorie.entity.MealRecord;
import com.calorie.mapper.MealRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FoodService 测试 — 重点测试删除食物时的关联检查逻辑
 */
@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private MealRecordMapper mealRecordMapper;

    private FoodService foodService;

    @BeforeEach
    void setUp() {
        foodService = new FoodService(mealRecordMapper);
    }

    @Test
    void removeById_whenMealRecordsExist_shouldThrowException() {
        when(mealRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            foodService.removeById(1L);
        });

        assertTrue(exception.getMessage().contains("3 条餐食记录引用"));
        assertTrue(exception.getMessage().contains("无法删除"));
        verify(mealRecordMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    void removeById_whenSingleMealRecord_shouldThrowException() {
        when(mealRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            foodService.removeById(42L);
        });

        assertTrue(exception.getMessage().contains("1 条餐食记录引用"));
        verify(mealRecordMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
    }
}
