package com.example.shoppingservice.repository;

import com.example.shoppingservice.model.ShoppingCart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@DataMongoTest
class ShoppingCartRepositoryTestIT {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void should_find_cart_by_id() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart shoppingCart = new ShoppingCart(cartId, Collections.emptyList(), LocalDateTime.now());
        shoppingCartRepository.save(shoppingCart).block();

        shoppingCartRepository.findShoppingCartById(cartId)
                .as(StepVerifier::create)
                .expectNextMatches(cart -> cart.getId().equals(cartId))
                .expectComplete()
                .verify();
    }

    @Test
    void should_delete_by_id() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart shoppingCart = new ShoppingCart(cartId, Collections.emptyList(), LocalDateTime.now());
        shoppingCartRepository.save(shoppingCart).block();

        shoppingCartRepository.deleteShoppingCartById(cartId)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        shoppingCartRepository.findShoppingCartById(cartId)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

}