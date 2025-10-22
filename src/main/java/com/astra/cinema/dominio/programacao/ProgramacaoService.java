package com.astra.cinema.dominio.programacao;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;
import java.util.Date;
import java.util.List;

public class ProgramacaoService {
    private final ProgramacaoRepositorio programacaoRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public ProgramacaoService(ProgramacaoRepositorio programacaoRepositorio, 
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

    public Programacao criarProgramacao(Date periodoInicio, Date periodoFim, List<SessaoId> sessoes) {
        if (periodoInicio == null || periodoFim == null) {
            throw new IllegalArgumentException("O período não pode ser nulo");
        }
        if (sessoes == null || sessoes.isEmpty()) {
            throw new IllegalArgumentException("A programação deve ter pelo menos uma sessão");
        }
        
        // Valida se todas as sessões estão disponíveis
        for (SessaoId sessaoId : sessoes) {
            var sessao = sessaoRepositorio.obterPorId(sessaoId);
            if (sessao.getStatus() != StatusSessao.DISPONIVEL) {
                throw new IllegalStateException("Apenas sessões disponíveis podem ser adicionadas à programação");
            }
        }
        
        var programacaoId = new ProgramacaoId(System.identityHashCode(sessoes)); // Simplificado
        var programacao = new Programacao(programacaoId, periodoInicio, periodoFim, sessoes);
        programacaoRepositorio.salvar(programacao);
        
        return programacao;
    }

    public void salvar(Programacao programacao) {
        programacaoRepositorio.salvar(programacao);
    }

    public Programacao obter(ProgramacaoId programacaoId) {
        return programacaoRepositorio.obterPorId(programacaoId);
    }
}
