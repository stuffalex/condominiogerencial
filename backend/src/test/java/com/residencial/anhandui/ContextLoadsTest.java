package com.residencial.anhandui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContextLoadsTest {

    @Test
    @DisplayName("Garante que o Spring Boot Context carrega com sucesso com Composition Root e SQLite")
    void contextLoads() {
    }
}
