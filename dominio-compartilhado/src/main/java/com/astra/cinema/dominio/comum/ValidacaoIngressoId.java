package com.astra.cinema.dominio.comum;

public class ValidacaoIngressoId {
    private final Integer valor;

    public ValidacaoIngressoId(Integer valor) {
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidacaoIngressoId that = (ValidacaoIngressoId) o;
        return valor != null && valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor != null ? valor.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ValidacaoIngressoId{" + valor + '}';
    }
}
