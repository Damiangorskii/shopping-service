package com.example.shoppingservice.service;

import com.example.shoppingservice.ProductDataProvider;
import com.example.shoppingservice.client.ProductClient;
import com.example.shoppingservice.model.*;
import com.example.shoppingservice.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShoppingServiceTest {

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

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ProductClient productClient;
    private ShoppingService shoppingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        shoppingService = new ShoppingService(shoppingCartRepository, productClient);
    }

    @Test
    void should_return_cart() {
        when(shoppingCartRepository.findShoppingCartById(any())).thenReturn(Mono.just(SHOPPING_CART));

        shoppingService.retrieveShoppingCart(UUID.randomUUID())
                .as(StepVerifier::create)
                .expectNext(SHOPPING_CART)
                .expectComplete()
                .verify();
    }

    @Test
    void should_return_not_found_for_non_existing_cart() {
        when(shoppingCartRepository.findShoppingCartById(any())).thenReturn(Mono.empty());

        shoppingService.retrieveShoppingCart(UUID.randomUUID())
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"Shopping cart not found\"");
                })
                .verify();
    }

    @Test
    void should_create_shopping_cart() {
        Product product1 = ProductDataProvider.getSimpleProduct();
        Product product2 = ProductDataProvider.getSimpleProduct();
        ShoppingCartRequestBody requestBody = new ShoppingCartRequestBody(List.of(product1.getId(), product2.getId()));
        List<Product> productList = Arrays.asList(product1, product2);
        ShoppingCart shoppingCart = new ShoppingCart(UUID.randomUUID(), productList);

        when(productClient.getAllProducts()).thenReturn(Flux.fromIterable(productList));
        when(shoppingCartRepository.save(any())).thenReturn(Mono.just(shoppingCart));

        shoppingService.createShoppingCart(requestBody)
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getId()).isNotNull();
                    assertThat(cart.getProducts()).containsExactlyInAnyOrderElementsOf(productList);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateShoppingCart_NoMatchingProducts() {
        ShoppingCartRequestBody requestBody = new ShoppingCartRequestBody(List.of(UUID.randomUUID()));

        when(productClient.getAllProducts()).thenReturn(Flux.empty());

        shoppingService.createShoppingCart(requestBody)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"No available products found\"");
                })
                .verify();
    }

    @Test
    void should_successfully_edit_shopping_cart() {
        UUID cartId = UUID.randomUUID();
        Product product1 = ProductDataProvider.getSimpleProduct();
        Product product2 = ProductDataProvider.getSimpleProduct();
        List<Product> productList = Arrays.asList(product1, product2);
        ShoppingCart existingCart = new ShoppingCart(cartId, Collections.emptyList());
        ShoppingCart updatedCart = new ShoppingCart(cartId, productList);

        when(productClient.getAllProducts()).thenReturn(Flux.fromIterable(productList));
        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.just(existingCart));
        when(shoppingCartRepository.save(any())).thenReturn(Mono.just(updatedCart));

        shoppingService.editShoppingCart(cartId, new ShoppingCartRequestBody(List.of(product1.getId(), product2.getId())))
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getId()).isEqualTo(cartId);
                    assertThat(cart.getProducts()).containsExactlyInAnyOrderElementsOf(productList);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void should_return_error_when_no_products_found() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart existingCart = new ShoppingCart(cartId, Collections.emptyList());

        when(productClient.getAllProducts()).thenReturn(Flux.empty());
        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.just(existingCart));

        shoppingService.editShoppingCart(cartId, new ShoppingCartRequestBody(List.of(UUID.randomUUID())))
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"No products found\"");
                })
                .verify();
    }

    @Test
    void should_return_error_when_cart_does_not_exist() {
        UUID cartId = UUID.randomUUID();
        Product product1 = ProductDataProvider.getSimpleProduct();
        Product product2 = ProductDataProvider.getSimpleProduct();
        List<Product> productList = Arrays.asList(product1, product2);
        when(productClient.getAllProducts()).thenReturn(Flux.fromIterable(productList));
        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.empty());

        shoppingService.editShoppingCart(cartId, new ShoppingCartRequestBody(List.of(UUID.randomUUID())))
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"No products found\"");
                })
                .verify();
    }

    @Test
    void should_add_products_to_shopping_cart() {
        UUID cartId = UUID.randomUUID();
        Product product1 = ProductDataProvider.getSimpleProduct();
        Product product2 = ProductDataProvider.getSimpleProduct();
        List<Product> productList = Arrays.asList(product1, product2);
        ShoppingCart existingCart = new ShoppingCart(cartId, Collections.singletonList(product1));
        ShoppingCart updatedCart = new ShoppingCart(cartId, productList);

        when(productClient.getAllProducts()).thenReturn(Flux.fromIterable(productList));
        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.just(existingCart));
        when(shoppingCartRepository.save(any())).thenReturn(Mono.just(updatedCart));

        shoppingService.addProductsToShoppingCart(cartId, List.of(product1.getId(), product2.getId()))
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getId()).isEqualTo(cartId);
                    assertThat(cart.getProducts()).containsExactlyInAnyOrderElementsOf(productList);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void should_return_add_not_found_when_no_matching_products_found() {
        UUID cartId = UUID.randomUUID();
        List<Product> productList = List.of(ProductDataProvider.getSimpleProduct());
        ShoppingCart existingCart = new ShoppingCart(cartId, productList);

        when(productClient.getAllProducts()).thenReturn(Flux.empty());
        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.just(existingCart));

        Mono<ShoppingCart> result = shoppingService.addProductsToShoppingCart(cartId, Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"No products found\"");
                })
                .verify();
    }

    @Test
    void should_return_add_error_not_found_when_shopping_cart_not_found() {
        UUID cartId = UUID.randomUUID();
        List<Product> productList = List.of(ProductDataProvider.getSimpleProduct());
        List<UUID> productIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        when(productClient.getAllProducts()).thenReturn(Flux.fromIterable(productList));
        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.empty());

        shoppingService.addProductsToShoppingCart(cartId, productIds)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"No products found\"");
                })
                .verify();
    }

    @Test
    void should_remove_products_from_cart() {
        UUID cartId = UUID.randomUUID();
        List<UUID> productIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        Product product1 = ProductDataProvider.getSimpleProduct();
        List<Product> productList = Collections.singletonList(product1);
        List<UUID> productsToRemove = Collections.singletonList(productIds.get(0));
        ShoppingCart existingCart = new ShoppingCart(cartId, productList);
        ShoppingCart updatedCart = new ShoppingCart(cartId, Collections.emptyList());

        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.just(existingCart));
        when(shoppingCartRepository.save(any())).thenReturn(Mono.just(updatedCart));

        shoppingService.removeProductsFromShoppingCart(cartId, productsToRemove)
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getId()).isEqualTo(cartId);
                    assertThat(cart.getProducts()).isEmpty();
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void should_return_unchanged_cart_if_products_to_remove_empty() {
        UUID cartId = UUID.randomUUID();
        List<UUID> productsToRemove = Collections.emptyList();
        ShoppingCart existingCart = new ShoppingCart(cartId, List.of(ProductDataProvider.getSimpleProduct()));

        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.just(existingCart));
        when(shoppingCartRepository.save(any())).thenReturn(Mono.just(existingCart));

        shoppingService.removeProductsFromShoppingCart(cartId, productsToRemove)
                .as(StepVerifier::create)
                .expectNext(existingCart)
                .expectComplete()
                .verify();
    }

    @Test
    void should_return_not_found_for_remove_products_when_cart_does_not_exist() {
        UUID cartId = UUID.randomUUID();
        List<UUID> productsToRemove = Collections.singletonList(UUID.randomUUID());

        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.empty());

        shoppingService.removeProductsFromShoppingCart(cartId, productsToRemove)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"Shopping cart not found\"");
                })
                .verify();
    }

    @Test
    void should_successfully_delete_cart() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart existingCart = new ShoppingCart(cartId, Collections.emptyList());

        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.just(existingCart));
        when(shoppingCartRepository.deleteShoppingCartById(cartId)).thenReturn(Mono.empty());

        shoppingService.deleteShoppingCart(cartId)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(shoppingCartRepository).findShoppingCartById(cartId);
        verify(shoppingCartRepository).deleteShoppingCartById(cartId);
    }

    @Test
    void should_return_not_found_for_delete_if_cart_does_not_exist() {
        UUID cartId = UUID.randomUUID();
        when(shoppingCartRepository.findShoppingCartById(cartId)).thenReturn(Mono.empty());

        shoppingService.deleteShoppingCart(cartId)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ResponseStatusException.class)
                            .hasMessage("404 NOT_FOUND \"Shopping cart not found\"");
                })
                .verify();

        verify(shoppingCartRepository).findShoppingCartById(cartId);
        verify(shoppingCartRepository, never()).deleteShoppingCartById(cartId);
    }

}