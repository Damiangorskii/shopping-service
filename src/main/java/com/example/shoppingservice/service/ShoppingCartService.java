package com.example.shoppingservice.service;

import com.example.shoppingservice.client.ProductClient;
import com.example.shoppingservice.model.Product;
import com.example.shoppingservice.model.ShoppingCart;
import com.example.shoppingservice.model.ShoppingCartRequestBody;
import com.example.shoppingservice.repository.ShoppingCartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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

    public Mono<ShoppingCart> editShoppingCart(final UUID cartId, final ShoppingCartRequestBody requestBody) {
        return productClient.getAllProducts()
                .filter(product -> requestBody.products().contains(product.getId()))
                .collectList()
                .zipWith(shoppingCartRepository.findShoppingCartById(cartId))
                .flatMap(tuple -> updateProducts(tuple.getT2(), tuple.getT1()))
                .flatMap(shoppingCartRepository::save);
    }

    public Mono<ShoppingCart> addProductsToShoppingCart(final UUID cartId, final List<UUID> productIds) {
        return productClient.getAllProducts()
                .filter(product -> productIds.contains(product.getId()))
                .collectList()
                .zipWith(shoppingCartRepository.findShoppingCartById(cartId))
                .flatMap(tuple -> addProducts(tuple.getT2(), tuple.getT1()))
                .flatMap(shoppingCartRepository::save);
    }

    public Mono<ShoppingCart> removeProductsFromShoppingCart(final UUID cartId, final List<UUID> productIds) {
        return shoppingCartRepository.findShoppingCartById(cartId)
                .flatMap(cart -> removeProducts(cart, productIds))
                .flatMap(shoppingCartRepository::save);
    }

    public Mono<Void> deleteShoppingCart(final UUID id) {
        return shoppingCartRepository.deleteShoppingCartById(id);
    }

    private Mono<ShoppingCart> updateProducts(final ShoppingCart shoppingCart, final List<Product> newProducts) {
        shoppingCart.setProducts(newProducts);
        return Mono.just(shoppingCart);
    }

    private Mono<ShoppingCart> removeProducts(final ShoppingCart shoppingCart, final List<UUID> productsToRemove) {
        List<Product> updatedProducts = shoppingCart.getProducts().stream()
                .filter(product -> !productsToRemove.contains(product.getId()))
                .toList();
        shoppingCart.setProducts(updatedProducts);
        return Mono.just(shoppingCart);
    }

    private Mono<ShoppingCart> addProducts(final ShoppingCart shoppingCart, final List<Product> newProducts) {
        List<Product> combinedProductList = Stream.concat(shoppingCart.getProducts().stream(), newProducts.stream())
                .distinct()
                .toList();
        shoppingCart.setProducts(combinedProductList);
        return Mono.just(shoppingCart);
    }
}