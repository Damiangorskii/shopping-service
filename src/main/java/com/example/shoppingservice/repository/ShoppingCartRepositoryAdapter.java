package com.example.shoppingservice.repository;

import com.example.shoppingservice.model.ShoppingCart;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShoppingCartRepositoryAdapter {

    private final ShoppingCartRepository shoppingCartRepository;

    public Mono<ShoppingCart> findShoppingCartById(UUID id) {
        return Mono.fromCallable(() -> shoppingCartRepository.findShoppingCartById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteShoppingCartById(UUID id) {
        return Mono.fromRunnable(() -> shoppingCartRepository.deleteShoppingCartById(id))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> deleteByInsertDateTimeBefore(LocalDateTime time) {
        return Mono.fromRunnable(() -> shoppingCartRepository.deleteByInsertDateTimeBefore(time))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<ShoppingCart> save(final ShoppingCart shoppingCart) {
        return Mono.fromCallable(() -> shoppingCartRepository.save(shoppingCart))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
