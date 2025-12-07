package com.astra.cinema.dominio.operacao;

import com.astra.cinema.dominio.comum.*;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de validação de ingressos.
 */
public interface ValidacaoIngressoRepositorio {

    /**
     * Salva uma validação de ingresso no banco de dados.
     */
    ValidacaoIngresso salvar(ValidacaoIngresso validacao);

    /**
     * Busca uma validação por ID.
     */
    Optional<ValidacaoIngresso> buscarPorId(ValidacaoIngressoId id);

    /**
     * Lista todas as validações de um funcionário.
     */
    List<ValidacaoIngresso> listarPorFuncionario(FuncionarioId funcionarioId);

    /**
     * Lista todas as validações de um ingresso específico.
     */
    List<ValidacaoIngresso> listarPorIngresso(IngressoId ingressoId);

    /**
     * Lista todas as validações (para histórico geral).
     */
    List<ValidacaoIngresso> listarTodas();
}
