package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.usuario.Cargo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioJpa, Integer> {
    boolean existsByNomeIgnoreCaseAndCargo(String nome, Cargo cargo);
    boolean existsByNomeIgnoreCaseAndCargoAndIdNot(String nome, Cargo cargo, Integer id);
    List<FuncionarioJpa> findByCargo(Cargo cargo);
}
