package com.calorie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（演示项目明文存储） */
    private String password;

    /** 性别：男/女 */
    private String gender;

    /** 年龄 */
    private Integer age;

    /** 登录令牌 */
    private String token;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
