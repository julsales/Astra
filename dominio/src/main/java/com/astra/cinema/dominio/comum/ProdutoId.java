package com.astra.cinema.dominio.comum;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;

import java.util.Objects;

public class ProdutoId {
    private final int id;

    public ProdutoId(int id) {
        this.id = exigirPositivo(id, "O id deve ser positivo");
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ProdutoId) {
            var produtoId = (ProdutoId) obj;
            return id == produtoId.id;
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
