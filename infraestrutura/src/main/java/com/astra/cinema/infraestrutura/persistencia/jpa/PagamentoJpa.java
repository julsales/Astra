package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.PagamentoId;
import com.astra.cinema.dominio.pagamento.Pagamento;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;
import com.astra.cinema.dominio.pagamento.StatusPagamento;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entidade JPA para Pagamento
 */
@Entity
@Table(name = "PAGAMENTO")
class PagamentoJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_pagamento")
    private Date dataPagamento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}

/**
 * Interface Spring Data JPA para Pagamento
 */

/**
 * Implementação do repositório de domínio usando JPA
 */
@Repository
class PagamentoRepositorioJpaImpl implements PagamentoRepositorio {

    private final PagamentoJpaRepository repository;

    public PagamentoRepositorioJpaImpl(PagamentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(Pagamento pagamento) {
        if (pagamento == null) {
            throw new IllegalArgumentException("O pagamento não pode ser nulo");
        }

        PagamentoJpa jpa = new PagamentoJpa();
        if (pagamento.getPagamentoId() != null && pagamento.getPagamentoId().getId() > 0) {
            jpa.setId(pagamento.getPagamentoId().getId());
        }
        jpa.setValor(pagamento.getValor());
        jpa.setStatus(pagamento.getStatus());
        jpa.setDataPagamento(pagamento.getDataPagamento());

        repository.save(jpa);
    }

    @Override
    public Pagamento obterPorId(PagamentoId pagamentoId) {
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }

        return repository.findById(pagamentoId.getId())
                .map(this::mapearParaDominio)
                .orElse(null);
    }

    @Override
    public List<Pagamento> buscarPorStatus(StatusPagamento status) {
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }

        return repository.findByStatus(status).stream()
                .map(this::mapearParaDominio)
                .collect(Collectors.toList());
    }

    private Pagamento mapearParaDominio(PagamentoJpa jpa) {
        return new Pagamento(
                new PagamentoId(jpa.getId()),
                jpa.getValor(),
                jpa.getStatus(),
                jpa.getDataPagamento()
        );
    }
}
