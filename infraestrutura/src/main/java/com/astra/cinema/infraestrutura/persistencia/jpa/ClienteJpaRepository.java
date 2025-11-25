package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteJpa, Integer> {
    Optional<ClienteJpa> findByEmail(String email);
}
