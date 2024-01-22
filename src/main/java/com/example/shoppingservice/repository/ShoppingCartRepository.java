package com.example.shoppingservice.repository;

import com.example.shoppingservice.model.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, String> {
    Optional<ShoppingCart> findShoppingCartById(UUID id);

    void deleteShoppingCartById(UUID id);

    void deleteByInsertDateTimeBefore(LocalDateTime time);

}
