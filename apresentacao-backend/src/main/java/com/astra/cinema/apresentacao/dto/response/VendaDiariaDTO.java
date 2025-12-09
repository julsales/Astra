package com.astra.cinema.apresentacao.dto.response;

import java.time.LocalDate;
import java.math.BigDecimal;

public class VendaDiariaDTO {
    private LocalDate data;
    private BigDecimal totalVendas;
    private Integer quantidadeIngressos;

    public VendaDiariaDTO() {
    }

    public VendaDiariaDTO(LocalDate data, BigDecimal totalVendas, Integer quantidadeIngressos) {
        this.data = data;
        this.totalVendas = totalVendas;
        this.quantidadeIngressos = quantidadeIngressos;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(BigDecimal totalVendas) {
        this.totalVendas = totalVendas;
    }

    public Integer getQuantidadeIngressos() {
        return quantidadeIngressos;
    }

    public void setQuantidadeIngressos(Integer quantidadeIngressos) {
        this.quantidadeIngressos = quantidadeIngressos;
    }
}
