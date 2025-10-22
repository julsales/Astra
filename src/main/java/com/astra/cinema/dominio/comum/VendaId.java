package com.astra.cinema.dominio.comum;

import java.util.Objects;

public class VendaId {
    private final int id;

    public VendaId(int id) {
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
        if (obj != null && obj instanceof VendaId) {
            var vendaId = (VendaId) obj;
            return id == vendaId.id;
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
