package com.astra.cinema.dominio.comum;

import java.util.Objects;

public class CompraId {
    private final int id;

    public CompraId(int id) {
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
        if (obj != null && obj instanceof CompraId) {
            var compraId = (CompraId) obj;
            return id == compraId.id;
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
