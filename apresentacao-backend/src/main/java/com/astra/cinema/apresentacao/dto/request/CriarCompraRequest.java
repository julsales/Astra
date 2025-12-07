package com.astra.cinema.apresentacao.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CriarCompraRequest {

    @NotNull(message = "Cliente ID é obrigatório")
    @Positive(message = "Cliente ID deve ser positivo")
    private Long clienteId;

    @NotNull(message = "Sessão ID é obrigatório")
    @Positive(message = "Sessão ID deve ser positivo")
    private Long sessaoId;

    @NotNull(message = "Assentos são obrigatórios")
    @Size(min = 1, message = "Pelo menos um assento deve ser selecionado")
    private List<String> assentos;

    private String tipoIngresso; // "INTEIRA", "MEIA", "PROMOCAO"

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Long sessaoId) {
        this.sessaoId = sessaoId;
    }

    public List<String> getAssentos() {
        return assentos;
    }

    public void setAssentos(List<String> assentos) {
        this.assentos = assentos;
    }

    public String getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(String tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }
}
