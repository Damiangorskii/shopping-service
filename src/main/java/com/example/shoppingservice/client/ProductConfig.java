package com.example.shoppingservice.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "internal.api.search-service")
@Getter
@Setter
public class ProductConfig {

    private String url;
}
