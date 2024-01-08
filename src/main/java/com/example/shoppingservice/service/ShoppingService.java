package com.example.shoppingservice.service;

import com.example.shoppingservice.client.ProductClient;
import com.example.shoppingservice.model.Product;
import com.example.shoppingservice.model.ShoppingCart;
import com.example.shoppingservice.model.ShoppingCartRequestBody;
import com.example.shoppingservice.repository.ShoppingCartRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ShoppingService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductClient productClient;

    public Mono<ShoppingCart> retrieveShoppingCart(final UUID id) {
        return shoppingCartRepository.findShoppingCartById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping cart not found"))));
    }

    public Mono<ShoppingCart> createShoppingCart(final ShoppingCartRequestBody requestBody) {
        return productClient.getAllProducts()
                .filter(product -> requestBody.products().contains(product.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No available products found"))))
                .collectList()
                .map(productList -> new ShoppingCart(UUID.randomUUID(), productList, LocalDateTime.now()))
                .flatMap(shoppingCartRepository::save);
    }

    public Mono<ShoppingCart> editShoppingCart(final UUID cartId, final ShoppingCartRequestBody requestBody) {
        return productClient.getAllProducts()
                .filter(product -> requestBody.products().contains(product.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found"))))
                .collectList()
                .zipWith(shoppingCartRepository.findShoppingCartById(cartId))
                .flatMap(tuple -> updateProducts(tuple.getT2(), tuple.getT1()))
                .flatMap(shoppingCartRepository::save)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping cart not found"))));
    }

    public Mono<ShoppingCart> addProductsToShoppingCart(final UUID cartId, final List<UUID> productIds) {
        return productClient.getAllProducts()
                .filter(product -> productIds.contains(product.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found"))))
                .collectList()
                .zipWith(shoppingCartRepository.findShoppingCartById(cartId))
                .flatMap(tuple -> addProducts(tuple.getT2(), tuple.getT1()))
                .flatMap(shoppingCartRepository::save)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping cart not found"))));
    }

    public Mono<ShoppingCart> removeProductsFromShoppingCart(final UUID cartId, final List<UUID> productIds) {
        return shoppingCartRepository.findShoppingCartById(cartId)
                .flatMap(cart -> removeProducts(cart, productIds))
                .flatMap(shoppingCartRepository::save)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping cart not found"))));
    }

    public Mono<Void> deleteShoppingCart(final UUID id) {
        return shoppingCartRepository.findShoppingCartById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping cart not found"))))
                .flatMap(shoppingCart -> shoppingCartRepository.deleteShoppingCartById(shoppingCart.getId()));
    }

    public Mono<Void> deleteOldCarts() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1);
        return shoppingCartRepository.deleteByInsertDateTimeBefore(oneMinuteAgo);
    }

    private Mono<ShoppingCart> updateProducts(final ShoppingCart shoppingCart, final List<Product> newProducts) {
        shoppingCart.setProducts(newProducts);
        return Mono.just(shoppingCart);
    }

    private Mono<ShoppingCart> removeProducts(final ShoppingCart shoppingCart, final List<UUID> productsToRemove) {
        if (CollectionUtils.isNotEmpty(productsToRemove)) {
            List<Product> updatedProducts = shoppingCart.getProducts().stream()
                    .filter(product -> !productsToRemove.contains(product.getId()))
                    .toList();
            shoppingCart.setProducts(updatedProducts);
        }
        return Mono.just(shoppingCart);
    }

    private Mono<ShoppingCart> addProducts(final ShoppingCart shoppingCart, final List<Product> newProducts) {
        if (CollectionUtils.isNotEmpty(newProducts)) {
            List<Product> combinedProductList = Stream.concat(shoppingCart.getProducts().stream(), newProducts.stream())
                    .distinct()
                    .toList();
            shoppingCart.setProducts(combinedProductList);
        }
        return Mono.just(shoppingCart);
    }
}