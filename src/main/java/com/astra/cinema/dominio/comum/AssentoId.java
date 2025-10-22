package com.astra.cinema.dominio.comum;

import java.util.Objects;

public class AssentoId {
    private final String valor;

    public AssentoId(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O valor não pode ser nulo ou vazio");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AssentoId) {
            var assentoId = (AssentoId) obj;
            return valor.equals(assentoId.valor);
        }
        return false;
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
