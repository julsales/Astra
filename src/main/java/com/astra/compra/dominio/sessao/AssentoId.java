package com.astra.compra.dominio.sessao;

import java.util.Objects;

public class AssentoId {
    private final String valor;

    public AssentoId(String valor) {
        Objects.requireNonNull(valor, "O valor do ID não pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("O ID do assento não pode estar em branco");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssentoId that = (AssentoId) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
