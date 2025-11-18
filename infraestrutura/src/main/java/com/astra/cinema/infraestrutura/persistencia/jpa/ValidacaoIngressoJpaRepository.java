package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidacaoIngressoJpaRepository extends JpaRepository<ValidacaoIngressoJpa, Integer> {

    List<ValidacaoIngressoJpa> findByFuncionarioId(Integer funcionarioId);

    List<ValidacaoIngressoJpa> findByIngressoId(Integer ingressoId);

    List<ValidacaoIngressoJpa> findAllByOrderByDataHoraValidacaoDesc();
}
