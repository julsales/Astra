package com.astra.cinema.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA para Compra
 * Representa uma compra de ingressos no banco de dados
 */
@Entity
@Table(name = "compra")
public class CompraJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;
    
    @Column(name = "status", nullable = false, length = 30)
    private String status;
    
    @Column(name = "pagamento_id")
    private Integer pagamentoId;
    
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    // Construtor padrão (obrigatório para JPA)
    public CompraJpa() {
    }

    // Construtor com parâmetros
    public CompraJpa(Integer id, Integer clienteId, String status, Integer pagamentoId) {
        this.id = id;
        this.clienteId = clienteId;
        this.status = status;
        this.pagamentoId = pagamentoId;
        this.criadoEm = LocalDateTime.now();
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Integer pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}

