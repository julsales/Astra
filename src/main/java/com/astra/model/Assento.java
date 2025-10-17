package com.astra.model;

import jakarta.persistence.*;

@Entity
@Table(name = "assentos")
public class Assento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @Column(nullable = false)
    private String identificacao; // Ex: A1, B5, C10

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAssento status = StatusAssento.DISPONIVEL;

    // Construtores
    public Assento() {
    }

    public Assento(Sessao sessao, String identificacao) {
        this.sessao = sessao;
        this.identificacao = identificacao;
    }

    // Métodos de negócio
    public void reservar() {
        if (this.status != StatusAssento.DISPONIVEL) {
            throw new IllegalStateException("Assento não está disponível");
        }
        this.status = StatusAssento.RESERVADO;
    }

    public void ocupar() {
        this.status = StatusAssento.OCUPADO;
    }

    public void liberar() {
        this.status = StatusAssento.DISPONIVEL;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public StatusAssento getStatus() {
        return status;
    }

    public void setStatus(StatusAssento status) {
        this.status = status;
    }

    public enum StatusAssento {
        DISPONIVEL,
        RESERVADO,
        OCUPADO
    }
}
