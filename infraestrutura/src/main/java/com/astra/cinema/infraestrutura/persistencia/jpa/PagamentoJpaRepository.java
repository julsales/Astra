package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.pagamento.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoJpaRepository extends JpaRepository<PagamentoJpa, Integer> {
    List<PagamentoJpa> findByStatus(StatusPagamento status);
}
