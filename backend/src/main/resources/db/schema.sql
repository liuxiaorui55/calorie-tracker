-- ============================================
-- 一日三餐热量统计 App - 建表 + 初始数据
-- ============================================

-- 如果数据库不存在则创建
CREATE DATABASE IF NOT EXISTS calorie_tracker
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE calorie_tracker;

-- ============================================
-- 食物表
-- ============================================
DROP TABLE IF EXISTS meal_record;
DROP TABLE IF EXISTS food;

CREATE TABLE food (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name VARCHAR(100) NOT NULL COMMENT '食物名称',
    category VARCHAR(50) NOT NULL COMMENT '分类：主食/肉类/蔬菜/水果/饮品/零食',
    calories INT NOT NULL COMMENT '每单位热量（kcal）',
    unit VARCHAR(20) NOT NULL COMMENT '计量单位（g/个/碗/杯/份）',
    default_quantity DECIMAL(10,1) NOT NULL DEFAULT 100 COMMENT '默认份量',
    protein DECIMAL(5,1) DEFAULT 0 COMMENT '每单位蛋白质（g）',
    fat DECIMAL(5,1) DEFAULT 0 COMMENT '每单位脂肪（g）',
    carbs DECIMAL(5,1) DEFAULT 0 COMMENT '每单位碳水（g）',
    created_at DATETIME DEFAULT NOW() COMMENT '创建时间'
) COMMENT '食物表';

-- ============================================
-- 餐食记录表
-- ============================================
CREATE TABLE meal_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    record_date DATE NOT NULL COMMENT '记录日期',
    meal_type VARCHAR(10) NOT NULL COMMENT '餐别：BREAKFAST/LUNCH/DINNER',
    food_id BIGINT NOT NULL COMMENT '食物ID',
    food_name VARCHAR(100) NOT NULL COMMENT '食物名称（冗余）',
    quantity DECIMAL(10,1) NOT NULL COMMENT '食用份量',
    total_calories INT NOT NULL COMMENT '小计热量（kcal）',
    created_at DATETIME DEFAULT NOW() COMMENT '创建时间',
    INDEX idx_date (record_date),
    INDEX idx_meal_type (record_date, meal_type)
) COMMENT '餐食记录表';

-- ============================================
-- 初始数据：约 30 种常见中国食物
-- ============================================

-- 主食（6种）
INSERT INTO food (name, category, calories, unit, default_quantity, protein, fat, carbs) VALUES
('米饭', '主食', 116, '100g', 100, 2.6, 0.3, 25.9),
('馒头', '主食', 223, '个', 100, 7.0, 1.1, 44.2),
('面条（煮）', '主食', 110, '100g', 100, 3.5, 0.3, 22.0),
('小米粥', '主食', 46, '碗', 250, 1.4, 0.7, 8.4),
('全麦面包', '主食', 246, '片', 50, 8.5, 3.4, 43.0),
('红薯', '主食', 86, '100g', 100, 1.6, 0.1, 20.1);

-- 肉类/蛋类/水产（6种）
INSERT INTO food (name, category, calories, unit, default_quantity, protein, fat, carbs) VALUES
('鸡蛋（煮）', '肉类', 144, '个', 60, 13.3, 8.8, 2.8),
('鸡胸肉', '肉类', 133, '100g', 100, 31.0, 1.2, 0.0),
('猪瘦肉', '肉类', 143, '100g', 100, 20.3, 6.2, 1.5),
('牛肉（瘦）', '肉类', 125, '100g', 100, 22.0, 3.0, 1.0),
('虾仁', '肉类', 99, '100g', 100, 20.0, 0.8, 0.0),
('三文鱼', '肉类', 208, '100g', 100, 20.4, 13.4, 0.0);

-- 蔬菜（6种）
INSERT INTO food (name, category, calories, unit, default_quantity, protein, fat, carbs) VALUES
('西兰花', '蔬菜', 34, '100g', 100, 3.0, 0.4, 4.3),
('番茄', '蔬菜', 18, '个', 150, 0.9, 0.2, 3.5),
('黄瓜', '蔬菜', 16, '根', 200, 0.8, 0.2, 2.9),
('菠菜', '蔬菜', 23, '100g', 100, 2.9, 0.4, 3.6),
('胡萝卜', '蔬菜', 41, '100g', 100, 1.0, 0.2, 9.6),
('大白菜', '蔬菜', 13, '100g', 100, 1.5, 0.1, 2.2);

-- 水果（6种）
INSERT INTO food (name, category, calories, unit, default_quantity, protein, fat, carbs) VALUES
('苹果', '水果', 52, '个', 200, 0.4, 0.2, 13.5),
('香蕉', '水果', 93, '根', 120, 1.4, 0.2, 22.0),
('橙子', '水果', 47, '个', 200, 0.8, 0.2, 11.0),
('葡萄', '水果', 69, '100g', 100, 0.7, 0.2, 17.0),
('西瓜', '水果', 30, '100g', 300, 0.6, 0.1, 6.9),
('草莓', '水果', 32, '100g', 100, 0.7, 0.3, 7.1);

-- 饮品（4种）
INSERT INTO food (name, category, calories, unit, default_quantity, protein, fat, carbs) VALUES
('全脂牛奶', '饮品', 65, '杯', 250, 3.0, 3.5, 5.0),
('豆浆（无糖）', '饮品', 31, '杯', 250, 3.0, 1.6, 1.2),
('酸奶（原味）', '饮品', 72, '杯', 200, 2.5, 2.7, 9.3),
('橙汁', '饮品', 45, '杯', 250, 0.7, 0.2, 10.4);

-- 零食（4种）
INSERT INTO food (name, category, calories, unit, default_quantity, protein, fat, carbs) VALUES
('核桃', '零食', 654, '份', 30, 15.0, 60.0, 13.7),
('杏仁', '零食', 579, '份', 30, 21.0, 50.0, 19.7),
('黑巧克力', '零食', 546, '份', 30, 4.9, 32.0, 59.0),
('苏打饼干', '零食', 408, '份', 100, 7.0, 8.0, 76.0);
