package com.example.shoppingservice;

import com.example.shoppingservice.model.Category;
import com.example.shoppingservice.model.Manufacturer;
import com.example.shoppingservice.model.Product;
import com.example.shoppingservice.model.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductDataProvider {

    public static Product getSimpleProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .name("Product 1")
                .description("description 1")
                .price(BigDecimal.valueOf(10.0))
                .manufacturer(Manufacturer.builder()
                        .id(UUID.randomUUID())
                        .name("Manufacturer 1")
                        .address("Address 1")
                        .contact("test@address.com")
                        .build())
                .categories(List.of(
                        Category.BABY_PRODUCTS,
                        Category.AUTOMOTIVE
                ))
                .createdAt(LocalDateTime.of(2023, 10, 19, 19, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 19, 19, 0))
                .reviews(List.of(
                        Review.builder()
                                .reviewerName("Reviewer 1")
                                .comment("Comment 1")
                                .rating(5)
                                .reviewDate(LocalDateTime.of(2023, 10, 19, 19, 0))
                                .build()
                ))
                .build();
    }
}
