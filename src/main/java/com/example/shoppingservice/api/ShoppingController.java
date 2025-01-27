package com.example.shoppingservice.api;

import com.example.shoppingservice.model.ShoppingCart;
import com.example.shoppingservice.model.ShoppingCartRequestBody;
import com.example.shoppingservice.service.ShoppingService;
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

    private final ShoppingService shoppingService;

    @PostMapping
    public Mono<ShoppingCart> createShoppingCart(final @RequestBody @Valid ShoppingCartRequestBody requestBody) {
        return shoppingService.createShoppingCart(requestBody);
    }

    @GetMapping("{cartId}")
    public Mono<ShoppingCart> getShoppingCart(final @PathVariable UUID cartId) {
        return shoppingService.retrieveShoppingCart(cartId);
    }

    @PutMapping("{cartId}")
    public Mono<ShoppingCart> updateShoppingCart(final @PathVariable UUID cartId, final @RequestBody @Valid ShoppingCartRequestBody requestBody) {
        return shoppingService.editShoppingCart(cartId, requestBody);
    }

    @PatchMapping("{cartId}/add")
    public Mono<ShoppingCart> addProductsToShoppingCart(final @PathVariable UUID cartId, final @RequestParam List<UUID> productIds) {
        return shoppingService.addProductsToShoppingCart(cartId, productIds);
    }

    @PatchMapping("{cartId}/remove")
    public Mono<ShoppingCart> removeProductsFromShoppingCart(final @PathVariable UUID cartId, final @RequestParam List<UUID> productIds) {
        return shoppingService.removeProductsFromShoppingCart(cartId, productIds);
    }

    @DeleteMapping("{cartId}")
    public Mono<Void> deleteShoppingCart(final @PathVariable UUID cartId) {
        return shoppingService.deleteShoppingCart(cartId);
    }
}
