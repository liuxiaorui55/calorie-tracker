package com.calorie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 热量统计应用启动类
 */
@SpringBootApplication
@MapperScan("com.calorie.mapper")
public class CalorieApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalorieApplication.class, args);
        System.out.println("====================================");
        System.out.println("  热量统计服务启动成功！");
        System.out.println("  http://localhost:8080");
        System.out.println("====================================");
    }
}
