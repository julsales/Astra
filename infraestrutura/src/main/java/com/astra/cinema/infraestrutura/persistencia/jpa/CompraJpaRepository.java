package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório Spring Data JPA para Compra
 */
@Repository
public interface CompraJpaRepository extends JpaRepository<CompraJpa, Integer> {
    List<CompraJpa> findByClienteId(Integer clienteId);
    List<CompraJpa> findByStatus(String status);
}

