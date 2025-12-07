package com.astra.cinema.dominio.validacao;

import com.astra.cinema.dominio.compra.Ingresso;

/**
 * PADRÃO DECORATOR - Decorator Abstrato
 * Classe base para decorators que adicionam validações extras.
 */
public abstract class ValidadorIngressoDecorator implements ValidadorIngresso {

    protected final ValidadorIngresso validadorBase;

    protected ValidadorIngressoDecorator(ValidadorIngresso validadorBase) {
        this.validadorBase = validadorBase;
    }

    @Override
    public ResultadoValidacao validar(Ingresso ingresso) {
        // Primeiro executa a validação do componente decorado
        ResultadoValidacao resultadoBase = validadorBase.validar(ingresso);

        // Se a validação base falhou, retorna sem executar validação adicional
        if (!resultadoBase.isValido()) {
            return resultadoBase;
        }

        // Executa validação adicional específica do decorator
        return validarAdicional(ingresso);
    }

    /**
     * Método abstrato para validação adicional específica de cada decorator.
     */
    protected abstract ResultadoValidacao validarAdicional(Ingresso ingresso);
}
