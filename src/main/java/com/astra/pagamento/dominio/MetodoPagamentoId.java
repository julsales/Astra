package com.astra.pagamento.dominio;

import java.util.Objects;

public class MetodoPagamentoId {
    private final Integer valor;

    public MetodoPagamentoId(Integer valor) {
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
        MetodoPagamentoId that = (MetodoPagamentoId) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return "MetodoPagamentoId{" + valor + '}';
    }
}
