package com.example.shoppingservice.api;

import com.example.shoppingservice.model.*;
import com.example.shoppingservice.service.ShoppingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ShoppingControllerTest {

    @Mock
    private ShoppingService shoppingService;

    @InjectMocks
    private ShoppingController shoppingController;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(shoppingController).build();
    }

    @Test
    void should_return_create_shopping_cart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .id(UUID.randomUUID())
                .products(List.of(Product.builder()
                        .id(UUID.randomUUID())
                        .name("Test product")
                        .description("Test description")
                        .price(BigDecimal.TEN)
                        .manufacturer(Manufacturer.builder()
                                .id(UUID.randomUUID())
                                .name("manufacturer name")
                                .address("address")
                                .contact("contact")
                                .build())
                        .categories(List.of(Category.BABY_PRODUCTS))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .reviews(List.of(Review.builder()
                                .reviewerName("Name")
                                .comment("Comment")
                                .rating(5)
                                .reviewDate(LocalDateTime.now())
                                .build()))
                        .build()))
                .build();
        when(shoppingService.createShoppingCart(any()))
                .thenReturn(Mono.just(shoppingCart));

        webTestClient.post().uri("/shopping/cart")
                .bodyValue(new ShoppingCartRequestBody(List.of(UUID.randomUUID())))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ShoppingCart.class);
    }
}
