package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório Spring Data JPA para Ingresso
 */
@Repository
public interface IngressoJpaRepository extends JpaRepository<IngressoJpa, Integer> {
    List<IngressoJpa> findByCompraId(Integer compraId);
    List<IngressoJpa> findByStatus(String status);
    List<IngressoJpa> findByStatusIn(java.util.List<String> statuses);
    java.util.Optional<IngressoJpa> findByQrCode(String qrCode);
}

