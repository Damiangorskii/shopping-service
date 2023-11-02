package com.example.shoppingservice.model;

import java.util.List;
import java.util.UUID;

public record ShoppingCartRequestBody(List<UUID> products) {
}
