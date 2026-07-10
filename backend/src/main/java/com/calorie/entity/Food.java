package com.calorie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食物实体
 */
@Data
@TableName("food")
public class Food {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 食物名称 */
    private String name;

    /** 分类：主食/肉类/蔬菜/水果/饮品/零食 */
    private String category;

    /** 每单位热量（kcal） */
    private Integer calories;

    /** 计量单位（g/个/碗/杯/份） */
    private String unit;

    /** 默认份量（每份多少g或多少个） */
    private BigDecimal defaultQuantity;

    /** 每单位蛋白质含量（g） */
    private BigDecimal protein;

    /** 每单位脂肪含量（g） */
    private BigDecimal fat;

    /** 每单位碳水含量（g） */
    private BigDecimal carbs;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
