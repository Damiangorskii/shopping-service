package com.example.shoppingservice.repository;

import com.example.shoppingservice.model.ShoppingCart;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ShoppingCartRepository extends ReactiveMongoRepository<ShoppingCart, String> {
    Mono<ShoppingCart> findShoppingCartById(UUID id);

    Mono<Void> deleteShoppingCartById(UUID id);

}
