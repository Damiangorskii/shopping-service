package com.example.shoppingservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@AllArgsConstructor
@Slf4j
public class ShoppingCartCleanupScheduler {

    private final ShoppingService shoppingService;

    @Scheduled(cron = "0 0/3 * * * *")
    public void cleanUpOldCarts() {
        shoppingService.deleteOldCarts()
                .doOnSuccess(s -> log.info("Successfully removed old carts"))
                .doOnError(err -> log.error("Error occurred during old carts removal"))
                .subscribe();
    }
}
