package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpa, Integer> {
    Optional<UsuarioJpa> findByEmail(String email);
}
