package com.astra.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "programacoes")
public class Programacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @ManyToMany
    @JoinTable(
        name = "programacao_sessoes",
        joinColumns = @JoinColumn(name = "programacao_id"),
        inverseJoinColumns = @JoinColumn(name = "sessao_id")
    )
    private List<Sessao> sessoes = new ArrayList<>();

    @Column(nullable = false)
    private Boolean ativa = true;

    // Construtores
    public Programacao() {
    }

    public Programacao(LocalDate dataInicio, LocalDate dataFim) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // Métodos de negócio
    public void adicionarSessao(Sessao sessao) {
        if (sessao.getStatus() != Sessao.StatusSessao.DISPONIVEL) {
            throw new IllegalStateException("Apenas sessões disponíveis podem ser adicionadas à programação");
        }
        this.sessoes.add(sessao);
    }

    public void validarSessoes() {
        for (Sessao sessao : this.sessoes) {
            if (sessao.getStatus() != Sessao.StatusSessao.DISPONIVEL) {
                throw new IllegalStateException("Todas as sessões devem estar disponíveis");
            }
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        this.sessoes = sessoes;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
