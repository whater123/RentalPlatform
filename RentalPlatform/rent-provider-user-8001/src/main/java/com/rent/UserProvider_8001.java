package com.rent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author w
 */
@SpringBootApplication
@MapperScan("com.rent.dao")
//@EnableEurekaClient
//@EnableDiscoveryClient
public class UserProvider_8001 {
    public static void main(String[] args) {
        SpringApplication.run(UserProvider_8001.class,args);
    }
}
