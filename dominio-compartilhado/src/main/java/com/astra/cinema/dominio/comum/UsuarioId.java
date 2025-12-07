package com.astra.cinema.dominio.comum;

public class UsuarioId {
    private Integer valor;

    public UsuarioId(Integer valor) {
        if (valor == null) {
            // Permitir null para criação - será gerado pelo banco
            this.valor = null;
        } else {
            this.valor = valor;
        }
    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioId usuarioId = (UsuarioId) o;
        return valor != null && valor.equals(usuarioId.valor);
    }

    @Override
    public int hashCode() {
        return valor != null ? valor.hashCode() : 0;
    }

    @Override
    public String toString() {
        return valor != null ? valor.toString() : "null";
    }
}
