package com.diy.jewelry;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.diy.jewelry.mapper")
@SpringBootApplication
public class DiyJewelryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiyJewelryApplication.class, args);
    }
}
