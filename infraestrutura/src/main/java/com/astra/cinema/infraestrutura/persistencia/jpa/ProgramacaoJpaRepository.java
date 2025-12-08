package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramacaoJpaRepository extends JpaRepository<ProgramacaoJpa, Integer> {
}
