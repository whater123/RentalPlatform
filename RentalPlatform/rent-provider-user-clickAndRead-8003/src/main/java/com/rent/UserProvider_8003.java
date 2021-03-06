package com.rent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@EnableEurekaClient
//@EnableDiscoveryClient
public class UserProvider_8003 {
    public static void main(String[] args) {
        SpringApplication.run(UserProvider_8003.class,args);
    }
}
