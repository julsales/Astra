package com.astra.cinema.dominio.comum;

import java.util.Objects;

public class IngressoId {
    private final int id;

    public IngressoId(int id) {
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
        if (obj != null && obj instanceof IngressoId) {
            var ingressoId = (IngressoId) obj;
            return id == ingressoId.id;
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
