package com.example.shoppingservice.api;

import com.example.shoppingservice.model.ShoppingCart;
import com.example.shoppingservice.model.ShoppingCartRequestBody;
import com.example.shoppingservice.service.ShoppingCartService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/shopping/cart")
@AllArgsConstructor
public class ShoppingController {

    private final ShoppingCartService shoppingCartService;

    @PostMapping
    public Mono<ShoppingCart> createShoppingCart(@RequestBody ShoppingCartRequestBody requestBody) {
        return shoppingCartService.createShoppingCart(requestBody);
    }
}
