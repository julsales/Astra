package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório Spring Data JPA para Produto
 */
@Repository
public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpa, Integer> {
}
