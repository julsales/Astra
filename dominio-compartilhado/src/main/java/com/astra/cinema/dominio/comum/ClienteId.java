package com.astra.cinema.dominio.comum;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;

import java.util.Objects;

public class ClienteId {
    private final int id;

    public ClienteId(int id) {
        this.id = exigirPositivo(id, "O id deve ser positivo");
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ClienteId) {
            var clienteId = (ClienteId) obj;
            return id == clienteId.id;
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
