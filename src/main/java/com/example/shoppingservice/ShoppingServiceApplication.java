package com.example.shoppingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ShoppingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingServiceApplication.class, args);
    }

}
