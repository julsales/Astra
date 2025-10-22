package com.astra.cinema.dominio.comum;

import java.util.Objects;

public class PagamentoId {
    private final int id;

    public PagamentoId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O id deve ser positivo");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PagamentoId) {
            var pagamentoId = (PagamentoId) obj;
            return id == pagamentoId.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
