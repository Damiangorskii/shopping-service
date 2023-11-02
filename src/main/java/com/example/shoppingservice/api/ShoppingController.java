package com.example.shoppingservice.api;

import com.example.shoppingservice.model.ShoppingCart;
import com.example.shoppingservice.model.ShoppingCartRequestBody;
import com.example.shoppingservice.service.ShoppingCartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shopping/cart")
@AllArgsConstructor
public class ShoppingController {

    private final ShoppingCartService shoppingCartService;

    @PostMapping
    public Mono<ShoppingCart> createShoppingCart(final @RequestBody @Valid ShoppingCartRequestBody requestBody) {
        return shoppingCartService.createShoppingCart(requestBody);
    }

    @GetMapping
    public Mono<ShoppingCart> getShoppingCart(final @RequestParam UUID cartId) {
        return shoppingCartService.retrieveShoppingCart(cartId);
    }

    @PutMapping
    public Mono<ShoppingCart> updateShoppingCart(final @RequestParam UUID cartId, final @RequestBody @Valid ShoppingCartRequestBody requestBody) {
        return shoppingCartService.editShoppingCart(cartId, requestBody);
    }

    @PatchMapping("/add")
    public Mono<ShoppingCart> addProductsToShoppingCart(final @RequestParam UUID cartId, final @RequestParam List<UUID> productIds) {
        return shoppingCartService.addProductsToShoppingCart(cartId, productIds);
    }

    @PatchMapping("/remove")
    public Mono<ShoppingCart> removeProductsFromShoppingCart(final @RequestParam UUID cartId, final @RequestParam List<UUID> productIds) {
        return shoppingCartService.removeProductsFromShoppingCart(cartId, productIds);
    }

    @DeleteMapping
    public Mono<Void> deleteShoppingCart(final @RequestParam UUID cartId) {
        return shoppingCartService.deleteShoppingCart(cartId);
    }
}
