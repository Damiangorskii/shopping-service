package com.example.shoppingservice.client;

import com.example.shoppingservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class ProductClient {

    private final WebClient webClient;

    private final ProductConfig config;

    @Autowired
    public ProductClient(WebClient.Builder webClientBuilder, ProductConfig config) {
        this.webClient = WebClient.builder().baseUrl(config.getUrl()).build();
        this.config = config;
    }

    public Flux<Product> getAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }
}
