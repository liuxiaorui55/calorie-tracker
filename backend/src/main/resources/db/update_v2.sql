-- ============================================
-- V2 升级脚本：用户系统 + 个性化建议
-- ============================================

USE calorie_tracker;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（明文，演示用）',
    gender VARCHAR(4) COMMENT '性别：男/女',
    age INT COMMENT '年龄',
    token VARCHAR(64) COMMENT '登录令牌',
    created_at DATETIME DEFAULT NOW() COMMENT '创建时间'
) COMMENT '用户表';

-- 餐食记录表增加用户ID
ALTER TABLE meal_record ADD COLUMN user_id BIGINT COMMENT '用户ID';
