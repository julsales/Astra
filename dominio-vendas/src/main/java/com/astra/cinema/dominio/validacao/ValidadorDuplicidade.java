package com.astra.cinema.dominio.validacao;

import com.astra.cinema.dominio.comum.IngressoId;
import com.astra.cinema.dominio.compra.Ingresso;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PADRÃO DECORATOR - Decorator Concreto
 * Adiciona validação anti-duplicidade (impede validar o mesmo ingresso duas vezes).
 * 
 * CORREÇÃO: Usa ConcurrentHashMap.newKeySet() para thread-safety.
 * O estado é gerenciado externamente via injeção, não como variável de instância.
 */
public class ValidadorDuplicidade extends ValidadorIngressoDecorator {

    private final Set<IngressoId> ingressosJaValidados;

    /**
     * Construtor que recebe o validador base e um Set thread-safe para rastrear validações.
     * Permite gerenciamento externo do estado (injeção de dependência).
     */
    public ValidadorDuplicidade(ValidadorIngresso validadorBase, Set<IngressoId> cacheValidacoes) {
        super(validadorBase);
        if (cacheValidacoes == null) {
            throw new IllegalArgumentException("Cache de validações não pode ser nulo");
        }
        this.ingressosJaValidados = cacheValidacoes;
    }

    /**
     * Construtor alternativo que cria seu próprio cache thread-safe.
     * Útil para uso standalone, mas não recomendado em produção.
     */
    public ValidadorDuplicidade(ValidadorIngresso validadorBase) {
        this(validadorBase, ConcurrentHashMap.newKeySet());
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
