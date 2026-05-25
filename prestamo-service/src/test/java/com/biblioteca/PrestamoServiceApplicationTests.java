package com.biblioteca;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrestamoServiceApplicationTests {

    @Test
    void contextLoads() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {});
    }

    @Test
    void mainMethodTest() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> PrestamoServiceApplication.main(new String[]{}));
    }
}
