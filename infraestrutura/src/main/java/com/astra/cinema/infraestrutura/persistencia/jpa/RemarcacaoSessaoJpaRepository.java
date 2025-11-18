package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemarcacaoSessaoJpaRepository extends JpaRepository<RemarcacaoSessaoJpa, Integer> {

    List<RemarcacaoSessaoJpa> findByFuncionarioId(Integer funcionarioId);

    List<RemarcacaoSessaoJpa> findByIngressoId(Integer ingressoId);

    List<RemarcacaoSessaoJpa> findAllByOrderByDataHoraRemarcacaoDesc();
}
