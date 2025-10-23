package com.astra.cinema.aplicacao.programacao;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.programacao.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import java.util.Date;
import java.util.List;

/**
 * Caso de uso: Criar programação semanal do cinema
 * Responsabilidade: Orquestrar a criação de programação validando sessões
 */
public class CriarProgramacaoUseCase {
    private final ProgramacaoRepositorio programacaoRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public CriarProgramacaoUseCase(ProgramacaoRepositorio programacaoRepositorio,
                                   SessaoRepositorio sessaoRepositorio) {
        if (programacaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de programações não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        
        this.programacaoRepositorio = programacaoRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public Programacao executar(Date periodoInicio, Date periodoFim, List<SessaoId> sessoes) {
        if (periodoInicio == null || periodoFim == null) {
            throw new IllegalArgumentException("As datas não podem ser nulas");
        }
        if (periodoInicio.after(periodoFim)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim");
        }
        if (sessoes == null || sessoes.isEmpty()) {
            throw new IllegalArgumentException("A programação deve ter pelo menos uma sessão");
        }
        
        // Valida se todas as sessões existem e estão disponíveis
        for (SessaoId sessaoId : sessoes) {
            var sessao = sessaoRepositorio.obterPorId(sessaoId);
            if (sessao.getStatus() != com.astra.cinema.dominio.sessao.StatusSessao.DISPONIVEL) {
                throw new IllegalStateException("Apenas sessões disponíveis podem ser adicionadas à programação");
            }
        }
        
        var programacaoId = new ProgramacaoId(System.identityHashCode(sessoes));
        var programacao = new Programacao(programacaoId, periodoInicio, periodoFim, sessoes);
        programacaoRepositorio.salvar(programacao);
        
        return programacao;
    }
}
