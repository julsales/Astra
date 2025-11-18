package com.astra.cinema.dominio.comum;

public class RemarcacaoId {
    private final Integer valor;

    public RemarcacaoId(Integer valor) {
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemarcacaoId that = (RemarcacaoId) o;
        return valor != null && valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor != null ? valor.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RemarcacaoId{" + valor + '}';
    }
}
