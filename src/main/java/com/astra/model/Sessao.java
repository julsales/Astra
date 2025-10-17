package com.astra.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessoes")
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSessao status = StatusSessao.DISPONIVEL;

    @Column(name = "assentos_disponiveis")
    private Integer assentosDisponiveis;

    @Column(nullable = false)
    private String idioma = "Português";

    private Boolean legendado = false;

    // Construtores
    public Sessao() {
    }

    public Sessao(Filme filme, Sala sala, LocalDateTime dataHora, BigDecimal preco) {
        this.filme = filme;
        this.sala = sala;
        this.dataHora = dataHora;
        this.preco = preco;
        this.assentosDisponiveis = sala.getCapacidade();
    }

    // Métodos de negócio
    public void marcarEsgotada() {
        if (this.assentosDisponiveis == 0) {
            this.status = StatusSessao.ESGOTADA;
        } else {
            throw new IllegalStateException("Ainda há assentos disponíveis");
        }
    }

    public void reservarAssentos(int quantidade) {
        if (quantidade > this.assentosDisponiveis) {
            throw new IllegalStateException("Assentos insuficientes");
        }
        this.assentosDisponiveis -= quantidade;
        if (this.assentosDisponiveis == 0) {
            this.status = StatusSessao.ESGOTADA;
        }
    }

    public void liberarAssentos(int quantidade) {
        this.assentosDisponiveis += quantidade;
        if (this.status == StatusSessao.ESGOTADA && this.assentosDisponiveis > 0) {
            this.status = StatusSessao.DISPONIVEL;
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public void setStatus(StatusSessao status) {
        this.status = status;
    }

    public Integer getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public void setAssentosDisponiveis(Integer assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Boolean getLegendado() {
        return legendado;
    }

    public void setLegendado(Boolean legendado) {
        this.legendado = legendado;
    }

    public enum StatusSessao {
        DISPONIVEL,
        ESGOTADA,
        CANCELADA,
        ENCERRADA
    }
}
