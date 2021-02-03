package com.rent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author w
 */
@SpringBootApplication
@MapperScan("com.rent")
public class ManagerProvider_8002 {
    public static void main(String[] args) {
        SpringApplication.run(ManagerProvider_8002.class,args);
    }
}
