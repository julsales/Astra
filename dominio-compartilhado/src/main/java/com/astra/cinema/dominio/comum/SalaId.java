package com.astra.cinema.dominio.comum;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;

import java.util.Objects;

/**
 * Value Object que identifica uma Sala de cinema de forma única.
 * 
 * A Sala é uma entidade do domínio de cinema com características fixas
 * (capacidade, tipo, equipamentos) que não mudam entre sessões.
 */
public class SalaId {
    private final int id;

    public SalaId(int id) {
        this.id = exigirPositivo(id, "O id da sala deve ser positivo");
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SalaId) {
            var salaId = (SalaId) obj;
            return id == salaId.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sala #" + id;
    }
}
