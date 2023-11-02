package com.example.shoppingservice.service;

import com.example.shoppingservice.client.ProductClient;
import com.example.shoppingservice.model.ShoppingCart;
import com.example.shoppingservice.model.ShoppingCartRequestBody;
import com.example.shoppingservice.repository.ShoppingCartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductClient productClient;

    public Mono<ShoppingCart> retrieveShoppingCart(final UUID id) {
        return shoppingCartRepository.findShoppingCartById(id);
    }

    public Mono<ShoppingCart> createShoppingCart(final ShoppingCartRequestBody requestBody) {
        return productClient.getAllProducts()
                .filter(product -> requestBody.products().contains(product.getId()))
                .collectList()
                .map(productList -> new ShoppingCart(UUID.randomUUID(), productList))
                .flatMap(shoppingCartRepository::save);
    }

    public Mono<ShoppingCart> editShoppingCart(final ShoppingCart shoppingCart) {
        return shoppingCartRepository.save(shoppingCart);
    }

    public Mono<Void> deleteShoppingCart(final UUID id) {
        return shoppingCartRepository.deleteShoppingCartById(id);
    }
}