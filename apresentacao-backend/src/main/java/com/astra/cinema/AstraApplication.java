package com.astra.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Classe principal da aplicação Astra Cinema
 * 
 * Sistema de gerenciamento de cinema desenvolvido com:
 * - Domain-Driven Design (DDD)
 * - Clean Architecture (Hexagonal)
 * - Spring Boot 3.5.6 + Java 17
 * - React Frontend
 * - H2 Database
 */
@SpringBootApplication
@EnableScheduling
public class AstraApplication {

    @PostConstruct
    public void init() {
        // Define o timezone padrão da JVM para Brasília (UTC-3)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        System.out.println("🌎 Timezone configurado: " + TimeZone.getDefault().getID());
    }

    public static void main(String[] args) {
        SpringApplication.run(AstraApplication.class, args);
    }

}
