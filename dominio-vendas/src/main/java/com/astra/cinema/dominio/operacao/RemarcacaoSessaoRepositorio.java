package com.astra.cinema.dominio.operacao;

import com.astra.cinema.dominio.comum.*;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de remarcação de sessões.
 */
public interface RemarcacaoSessaoRepositorio {

    /**
     * Salva uma remarcação de sessão no banco de dados.
     */
    RemarcacaoSessao salvar(RemarcacaoSessao remarcacao);

    /**
     * Busca uma remarcação por ID.
     */
    Optional<RemarcacaoSessao> buscarPorId(RemarcacaoId id);

    /**
     * Lista todas as remarcações de um funcionário.
     */
    List<RemarcacaoSessao> listarPorFuncionario(FuncionarioId funcionarioId);

    /**
     * Lista todas as remarcações de um ingresso específico.
     */
    List<RemarcacaoSessao> listarPorIngresso(IngressoId ingressoId);

    /**
     * Lista todas as remarcações (para histórico geral).
     */
    List<RemarcacaoSessao> listarTodas();
}
