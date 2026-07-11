package com.calorie.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Food 实体校验测试
 */
class FoodValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validFood_shouldPassValidation() {
        Food food = new Food();
        food.setName("米饭");
        food.setCategory("主食");
        food.setCalories(116);
        food.setUnit("g");
        food.setDefaultQuantity(new BigDecimal("100"));

        Set<ConstraintViolation<Food>> violations = validator.validate(food);
        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyName_shouldFailValidation() {
        Food food = new Food();
        food.setCategory("主食");
        food.setCalories(116);
        food.setUnit("g");
        food.setDefaultQuantity(new BigDecimal("100"));

        Set<ConstraintViolation<Food>> violations = validator.validate(food);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("食物名称不能为空")));
    }

    @Test
    void nullCalories_shouldFailValidation() {
        Food food = new Food();
        food.setName("米饭");
        food.setCategory("主食");
        food.setUnit("g");
        food.setDefaultQuantity(new BigDecimal("100"));

        Set<ConstraintViolation<Food>> violations = validator.validate(food);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("热量不能为空")));
    }

    @Test
    void negativeCalories_shouldFailValidation() {
        Food food = new Food();
        food.setName("米饭");
        food.setCategory("主食");
        food.setCalories(-10);
        food.setUnit("g");
        food.setDefaultQuantity(new BigDecimal("100"));

        Set<ConstraintViolation<Food>> violations = validator.validate(food);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("热量必须大于0")));
    }

    @Test
    void blankCategory_shouldFailValidation() {
        Food food = new Food();
        food.setName("米饭");
        food.setCalories(116);
        food.setUnit("g");
        food.setDefaultQuantity(new BigDecimal("100"));

        Set<ConstraintViolation<Food>> violations = validator.validate(food);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("食物分类不能为空")));
    }
}
