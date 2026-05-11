package com.example.piagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * PiAgent 应用启动类
 *
 * 设计思想：
 * 1. 采用 Spring Boot 自动配置机制，让框架开箱即用
 * 2. @EnableConfigurationProperties 启用配置绑定，使 application.yml 中的配置生效
 *
 * 为什么不使用 @EnableAutoConfiguration？
 * 因为我们希望显式控制哪些组件被加载，由配置类统一管理
 */
@SpringBootApplication
@EnableConfigurationProperties
public class PiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiAgentApplication.class, args);
    }
}
