package com.calorie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 餐食记录实体
 */
@Data
@TableName("meal_record")
public class MealRecord {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 餐别：BREAKFAST / LUNCH / DINNER */
    private String mealType;

    /** 关联食物ID */
    private Long foodId;

    /** 食物名称（冗余字段，方便展示） */
    private String foodName;

    /** 食用份量 */
    private BigDecimal quantity;

    /** 小计热量 = 食物单位热量 × (份量 / 默认份量) */
    private Integer totalCalories;

    /** 用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
