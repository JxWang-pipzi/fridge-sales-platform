package com.fridge.sales;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 冰箱销售网站后端服务启动类
 */
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@MapperScan("com.fridge.sales.mapper")
@EnableCaching
@EnableAsync
public class FridgeSalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FridgeSalesApplication.class, args);
        System.out.println("========================================");
        System.out.println("  冰箱销售网站后端服务启动成功！");
        System.out.println("  访问地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}
