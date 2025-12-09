package com.astra.cinema.apresentacao.dto.response;

import java.math.BigDecimal;

public class FilmePopularDTO {
    private Integer id;
    private String titulo;
    private Integer totalIngressos;
    private BigDecimal receitaTotal;

    public FilmePopularDTO() {
    }

    public FilmePopularDTO(Integer id, String titulo, Integer totalIngressos, BigDecimal receitaTotal) {
        this.id = id;
        this.titulo = titulo;
        this.totalIngressos = totalIngressos;
        this.receitaTotal = receitaTotal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalIngressos() {
        return totalIngressos;
    }

    public void setTotalIngressos(Integer totalIngressos) {
        this.totalIngressos = totalIngressos;
    }

    public BigDecimal getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(BigDecimal receitaTotal) {
        this.receitaTotal = receitaTotal;
    }
}
