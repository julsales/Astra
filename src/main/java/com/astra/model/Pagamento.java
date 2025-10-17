package com.astra.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @OneToOne
    @JoinColumn(name = "venda_bomboniere_id")
    private VendaBomboniere vendaBomboniere;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @Column(name = "codigo_autorizacao")
    private String codigoAutorizacao;

    @Column(name = "mensagem_gateway")
    private String mensagemGateway;

    @PrePersist
    protected void onCreate() {
        dataPagamento = LocalDateTime.now();
    }

    // Construtores
    public Pagamento() {
    }

    public Pagamento(BigDecimal valor, FormaPagamento formaPagamento) {
        this.valor = valor;
        this.formaPagamento = formaPagamento;
    }

    // Métodos de negócio
    public void autorizar(String codigoAutorizacao) {
        this.status = StatusPagamento.SUCESSO;
        this.codigoAutorizacao = codigoAutorizacao;
    }

    public void recusar(String mensagem) {
        this.status = StatusPagamento.FALHA;
        this.mensagemGateway = mensagem;
    }

    public void cancelar() {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos pendentes podem ser cancelados");
        }
        this.status = StatusPagamento.CANCELADO;
    }

    public void estornar() {
        if (this.status != StatusPagamento.SUCESSO) {
            throw new IllegalStateException("Apenas pagamentos bem-sucedidos podem ser estornados");
        }
        this.status = StatusPagamento.ESTORNADO;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public VendaBomboniere getVendaBomboniere() {
        return vendaBomboniere;
    }

    public void setVendaBomboniere(VendaBomboniere vendaBomboniere) {
        this.vendaBomboniere = vendaBomboniere;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getCodigoAutorizacao() {
        return codigoAutorizacao;
    }

    public void setCodigoAutorizacao(String codigoAutorizacao) {
        this.codigoAutorizacao = codigoAutorizacao;
    }

    public String getMensagemGateway() {
        return mensagemGateway;
    }

    public void setMensagemGateway(String mensagemGateway) {
        this.mensagemGateway = mensagemGateway;
    }

    public enum FormaPagamento {
        CARTAO_CREDITO,
        CARTAO_DEBITO,
        PIX,
        DINHEIRO
    }

    public enum StatusPagamento {
        PENDENTE,
        SUCESSO,
        FALHA,
        CANCELADO,
        ESTORNADO
    }
}
