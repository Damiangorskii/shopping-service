package com.example.shoppingservice.repository;

import com.example.shoppingservice.model.ShoppingCart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ShoppingCartRepositoryTestIT {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void should_find_cart_by_id() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart shoppingCart = new ShoppingCart(cartId, Collections.emptyList(), LocalDateTime.now());
        shoppingCartRepository.save(shoppingCart);

        Optional<ShoppingCart> foundCart = shoppingCartRepository.findShoppingCartById(cartId);
        assertTrue(foundCart.isPresent());
        assertEquals(cartId, foundCart.get().getId());
    }

    @Test
    void should_delete_by_id() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart shoppingCart = new ShoppingCart(cartId, Collections.emptyList(), LocalDateTime.now());
        shoppingCartRepository.save(shoppingCart);

        shoppingCartRepository.deleteShoppingCartById(cartId);

        Optional<ShoppingCart> foundCart = shoppingCartRepository.findShoppingCartById(cartId);
        assertFalse(foundCart.isPresent());
    }

}