package com.astra.cinema.dominio.validacao;

import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;

/**
 * PADRÃO DECORATOR - Componente Base
 * Validador básico de ingresso (sem decorações).
 */
public class ValidadorIngressoBase implements ValidadorIngresso {

    @Override
    public ResultadoValidacao validar(Ingresso ingresso) {
        if (ingresso == null) {
            return new ResultadoValidacao(false, "Ingresso não pode ser nulo");
        }

        if (ingresso.getStatus() != StatusIngresso.ATIVO) {
            return new ResultadoValidacao(false,
                "Ingresso não está ativo. Status: " + ingresso.getStatus());
        }

        return new ResultadoValidacao(true, "Ingresso válido");
    }
}
