package com.astra.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "filmes")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String sinopse;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(name = "classificacao_indicativa")
    private String classificacaoIndicativa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFilme status = StatusFilme.EM_CARTAZ;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;

    private String genero;

    private String diretor;

    // Construtores
    public Filme() {
    }

    public Filme(String titulo, String sinopse, Integer duracaoMinutos) {
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.duracaoMinutos = duracaoMinutos;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getClassificacaoIndicativa() {
        return classificacaoIndicativa;
    }

    public void setClassificacaoIndicativa(String classificacaoIndicativa) {
        this.classificacaoIndicativa = classificacaoIndicativa;
    }

    public StatusFilme getStatus() {
        return status;
    }

    public void setStatus(StatusFilme status) {
        this.status = status;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    // Enumeração de Status
    public enum StatusFilme {
        EM_CARTAZ,
        EM_BREVE,
        RETIRADO
    }
}
