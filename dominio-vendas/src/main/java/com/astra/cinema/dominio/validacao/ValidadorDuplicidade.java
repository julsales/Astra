package com.astra.cinema.dominio.validacao;

import com.astra.cinema.dominio.comum.IngressoId;
import com.astra.cinema.dominio.compra.Ingresso;

import java.util.HashSet;
import java.util.Set;

/**
 * PADRÃO DECORATOR - Decorator Concreto
 * Adiciona validação anti-duplicidade (impede validar o mesmo ingresso duas vezes).
 */
public class ValidadorDuplicidade extends ValidadorIngressoDecorator {

    private final Set<IngressoId> ingressosJaValidados = new HashSet<>();

    public ValidadorDuplicidade(ValidadorIngresso validadorBase) {
        super(validadorBase);
    }

    @Override
    protected ResultadoValidacao validarAdicional(Ingresso ingresso) {
        IngressoId id = ingresso.getIngressoId();

        if (ingressosJaValidados.contains(id)) {
            return new ResultadoValidacao(false,
                "⚠️  FRAUDE DETECTADA! Ingresso já foi validado anteriormente");
        }

        // Marca como validado
        ingressosJaValidados.add(id);

        return new ResultadoValidacao(true, "Primeira validação deste ingresso");
    }

    /**
     * Limpa o histórico de validações (útil para testes).
     */
    public void limparHistorico() {
        ingressosJaValidados.clear();
    }

    /**
     * Retorna quantidade de ingressos já validados.
     */
    public int getQuantidadeValidados() {
        return ingressosJaValidados.size();
    }
}
