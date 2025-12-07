package com.astra.cinema.dominio.comum;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;

import java.util.Objects;

public class ProgramacaoId {
    private final int id;

    public ProgramacaoId() {
        this((int) (System.nanoTime() & 0x7fffffff) | 1);
    }

    public ProgramacaoId(int id) {
        this.id = exigirPositivo(id, "O id deve ser positivo");
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ProgramacaoId) {
            var programacaoId = (ProgramacaoId) obj;
            return id == programacaoId.id;
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
