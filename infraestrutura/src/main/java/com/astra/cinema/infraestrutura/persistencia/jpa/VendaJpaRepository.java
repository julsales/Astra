package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface Spring Data JPA para Venda
 */
@Repository
public interface VendaJpaRepository extends JpaRepository<VendaJpa, Integer> {
    List<VendaJpa> findByStatus(String status);
    List<VendaJpa> findByCompraId(Integer compraId);
}

