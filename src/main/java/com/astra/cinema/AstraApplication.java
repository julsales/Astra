package com.astra.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
public class AstraApplication {

    public static void main(String[] args) {
        SpringApplication.run(AstraApplication.class, args);
    }

}
