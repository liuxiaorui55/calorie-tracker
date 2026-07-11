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
 * MealRecord 实体校验测试
 */
class MealRecordValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validMealRecord_shouldPassValidation() {
        MealRecord record = new MealRecord();
        record.setMealType("BREAKFAST");
        record.setFoodId(1L);
        record.setQuantity(new BigDecimal("1.5"));

        Set<ConstraintViolation<MealRecord>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankMealType_shouldFailValidation() {
        MealRecord record = new MealRecord();
        record.setFoodId(1L);
        record.setQuantity(new BigDecimal("1.5"));

        Set<ConstraintViolation<MealRecord>> violations = validator.validate(record);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("餐别不能为空")));
    }

    @Test
    void nullFoodId_shouldFailValidation() {
        MealRecord record = new MealRecord();
        record.setMealType("LUNCH");
        record.setQuantity(new BigDecimal("2"));

        Set<ConstraintViolation<MealRecord>> violations = validator.validate(record);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("食物ID不能为空")));
    }

    @Test
    void nullQuantity_shouldFailValidation() {
        MealRecord record = new MealRecord();
        record.setMealType("DINNER");
        record.setFoodId(1L);

        Set<ConstraintViolation<MealRecord>> violations = validator.validate(record);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("食用份量不能为空")));
    }

    @Test
    void negativeQuantity_shouldFailValidation() {
        MealRecord record = new MealRecord();
        record.setMealType("DINNER");
        record.setFoodId(1L);
        record.setQuantity(new BigDecimal("-0.5"));

        Set<ConstraintViolation<MealRecord>> violations = validator.validate(record);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("食用份量必须大于0")));
    }

    @Test
    void zeroQuantity_shouldFailValidation() {
        MealRecord record = new MealRecord();
        record.setMealType("DINNER");
        record.setFoodId(1L);
        record.setQuantity(BigDecimal.ZERO);

        Set<ConstraintViolation<MealRecord>> violations = validator.validate(record);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("食用份量必须大于0")));
    }
}
