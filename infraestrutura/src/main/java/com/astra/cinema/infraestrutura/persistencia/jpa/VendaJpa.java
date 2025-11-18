package com.astra.cinema.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA para Venda
 * Representa uma venda da bomboniere no banco de dados
 */
@Entity
@Table(name = "venda")
public class VendaJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "produto_id", nullable = false)
    private Integer produtoId;
    
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;
    
    @Column(name = "pagamento_id")
    private Integer pagamentoId;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    // Construtor padrão (obrigatório para JPA)
    public VendaJpa() {
    }

    // Construtor com parâmetros
    public VendaJpa(Integer id, Integer produtoId, Integer quantidade, Integer pagamentoId, String status) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.pagamentoId = pagamentoId;
        this.status = status;
        this.criadoEm = LocalDateTime.now();
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Integer pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}

