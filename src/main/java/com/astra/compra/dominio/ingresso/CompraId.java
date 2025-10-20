package com.astra.compra.dominio.ingresso;

import java.util.Objects;

public class CompraId {
    private final Integer valor;

    public CompraId(Integer valor) {
        Objects.requireNonNull(valor, "O valor do ID não pode ser nulo");
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompraId that = (CompraId) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
