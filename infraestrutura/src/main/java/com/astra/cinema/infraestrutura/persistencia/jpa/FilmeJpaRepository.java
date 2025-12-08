package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.filme.StatusFilme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmeJpaRepository extends JpaRepository<FilmeJpa, Integer> {
    List<FilmeJpa> findByStatus(StatusFilme status);
}
