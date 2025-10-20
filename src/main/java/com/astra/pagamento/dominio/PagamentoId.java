package com.astra.pagamento.dominio;

import java.util.Objects;

public class PagamentoId {
    private final Integer valor;

    public PagamentoId(Integer valor) {
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
        PagamentoId that = (PagamentoId) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return "PagamentoId{" + valor + '}';
    }
}
