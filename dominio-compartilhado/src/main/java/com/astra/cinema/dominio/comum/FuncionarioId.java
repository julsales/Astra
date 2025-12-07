package com.astra.cinema.dominio.comum;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;

import java.util.Objects;

/**
 * Value Object para identificar funcionários de maneira consistente entre camadas.
 */
public class FuncionarioId {
    private final int valor;

    public FuncionarioId(int valor) {
        this.valor = exigirPositivo(valor, "O id do funcionário deve ser positivo");
    }

    public int getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FuncionarioId that = (FuncionarioId) obj;
        return valor == that.valor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return Integer.toString(valor);
    }
}
