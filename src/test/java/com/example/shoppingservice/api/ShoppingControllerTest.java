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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ShoppingControllerTest {

    private static final ShoppingCart SHOPPING_CART = ShoppingCart.builder()
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
    private static final String NOT_UUID = "some-not-uuid-string";
    private static final RuntimeException ERROR = new RuntimeException("some-error");
    private static final ShoppingCartRequestBody BODY = new ShoppingCartRequestBody(List.of(UUID.randomUUID()));
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
        when(shoppingService.createShoppingCart(any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.post().uri("/shopping/cart")
                .bodyValue(BODY)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ShoppingCart.class);
    }

    @Test
    void should_return_error_if_wrong_shopping_cart_url() {
        when(shoppingService.createShoppingCart(any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.post().uri("/shopping/cat")
                .bodyValue(BODY)
                .exchange()
                .expectStatus()
                .is4xxClientError();
        ;
    }

    @Test
    void should_return_error_if_create_cart_returned_error() {
        when(shoppingService.createShoppingCart(any()))
                .thenReturn(Mono.error(ERROR));

        webTestClient.post().uri("/shopping/cart")
                .bodyValue(BODY)
                .exchange()
                .expectStatus()
                .is5xxServerError();
        ;
    }

    @Test
    void should_return_bad_request_for_create_cart() {
        when(shoppingService.createShoppingCart(any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.post().uri("/shopping/cart")
                .bodyValue(new ShoppingCartRequestBody(null))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_empty_cart() {
        ShoppingCart shoppingCart = new ShoppingCart(UUID.randomUUID(), Collections.emptyList());
        when(shoppingService.createShoppingCart(any()))
                .thenReturn(Mono.just(shoppingCart));

        webTestClient.post().uri("/shopping/cart")
                .bodyValue(BODY)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ShoppingCart.class)
                .isEqualTo(shoppingCart);
    }

    @Test
    void should_return_shopping_cart() {
        when(shoppingService.retrieveShoppingCart(any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.get().uri("/shopping/cart/{cartId}", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ShoppingCart.class)
                .isEqualTo(SHOPPING_CART);
    }

    @Test
    void should_return_error_if_get_cart_returned_error() {
        when(shoppingService.retrieveShoppingCart(any()))
                .thenReturn(Mono.error(ERROR));

        webTestClient.get().uri("/shopping/cart/{cartId}", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    void should_return_bad_request_if_not_uuid() {
        when(shoppingService.retrieveShoppingCart(any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.get().uri("/shopping/cart/{cartId}", NOT_UUID)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_updated_shopping_cart() {
        when(shoppingService.editShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.put().uri("/shopping/cart/{cartId}", UUID.randomUUID())
                .bodyValue(BODY)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ShoppingCart.class)
                .isEqualTo(SHOPPING_CART);
    }

    @Test
    void should_return_error_if_edit_returned_error() {
        when(shoppingService.editShoppingCart(any(), any()))
                .thenReturn(Mono.error(ERROR));

        webTestClient.put().uri("/shopping/cart/{cartId}", UUID.randomUUID())
                .bodyValue(new ShoppingCartRequestBody(List.of(UUID.randomUUID())))
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    void should_return_bad_request_if_cart_id_edit_not_uuid() {
        when(shoppingService.editShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.put().uri("/shopping/cart/{cartId}", NOT_UUID)
                .bodyValue(new ShoppingCartRequestBody(List.of(UUID.randomUUID())))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_bad_request_if_edit_body_not_valid() {
        when(shoppingService.editShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.put().uri("/shopping/cart/{cartId}", UUID.randomUUID())
                .bodyValue(new ShoppingCartRequestBody(null))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_shopping_cart_with_added_products() {
        when(shoppingService.addProductsToShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/add?productIds=0073bddf-dcd5-4715-b914-eb48c35b9016", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ShoppingCart.class)
                .isEqualTo(SHOPPING_CART);
    }

    @Test
    void should_return_error_id_add_products_returned_error() {
        when(shoppingService.addProductsToShoppingCart(any(), any()))
                .thenReturn(Mono.error(ERROR));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/add?productIds=0073bddf-dcd5-4715-b914-eb48c35b9016", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    void should_return_bad_request_if_query_params_not_uuid() {
        when(shoppingService.addProductsToShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/add?productIds=8c35b9016", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_bad_request_if_add_products_path_variable_not_uuid() {
        when(shoppingService.addProductsToShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/add?productIds=0073bddf-dcd5-4715-b914-eb48c35b9016", NOT_UUID)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_shopping_cart_with_removed_products() {
        when(shoppingService.removeProductsFromShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/remove?productIds=0073bddf-dcd5-4715-b914-eb48c35b9016", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ShoppingCart.class)
                .isEqualTo(SHOPPING_CART);
    }

    @Test
    void should_return_error_id_remove_products_returned_error() {
        when(shoppingService.removeProductsFromShoppingCart(any(), any()))
                .thenReturn(Mono.error(ERROR));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/remove?productIds=0073bddf-dcd5-4715-b914-eb48c35b9016", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    void should_return_bad_request_if_query_params_not_uuid_for_remove_products() {
        when(shoppingService.removeProductsFromShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/remove?productIds=8c35b9016", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_bad_request_if_remove_products_path_variable_not_uuid() {
        when(shoppingService.removeProductsFromShoppingCart(any(), any()))
                .thenReturn(Mono.just(SHOPPING_CART));

        webTestClient.patch()
                .uri("/shopping/cart/{cartId}/remove?productIds=0073bddf-dcd5-4715-b914-eb48c35b9016", NOT_UUID)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void should_return_empty_for_removed_cart() {
        when(shoppingService.deleteShoppingCart(any()))
                .thenReturn(Mono.empty());

        webTestClient.delete().uri("/shopping/cart/{cartId}", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_return_error_if_delete_returned_error() {
        when(shoppingService.deleteShoppingCart(any()))
                .thenReturn(Mono.error(ERROR));

        webTestClient.delete().uri("/shopping/cart/{cartId}", UUID.randomUUID())
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    void should_return_bad_request_for_delete_when_not_uuid() {
        when(shoppingService.deleteShoppingCart(any()))
                .thenReturn(Mono.empty());

        webTestClient.delete().uri("/shopping/cart/{cartId}", NOT_UUID)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
