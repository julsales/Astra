package com.astra.cinema.aplicacao.programacao;

import com.astra.cinema.dominio.comum.ProgramacaoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.programacao.Programacao;
import com.astra.cinema.dominio.programacao.ProgramacaoRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import java.util.Date;
import java.util.List;

/**
 * Service de Programação - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class ProgramacaoService {
    private final ProgramacaoRepositorio programacaoRepositorio;
    private final CriarProgramacaoUseCase criarProgramacaoUseCase;

    public ProgramacaoService(ProgramacaoRepositorio programacaoRepositorio,
                             SessaoRepositorio sessaoRepositorio) {
        if (programacaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de programações não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }

        this.programacaoRepositorio = programacaoRepositorio;
        this.criarProgramacaoUseCase = new CriarProgramacaoUseCase(programacaoRepositorio, sessaoRepositorio);
    }

    public Programacao criarProgramacao(Date periodoInicio, Date periodoFim, List<SessaoId> sessoes) {
        return criarProgramacaoUseCase.executar(periodoInicio, periodoFim, sessoes);
    }

    public void salvar(Programacao programacao) {
        programacaoRepositorio.salvar(programacao);
    }

    public Programacao obter(ProgramacaoId programacaoId) {
        return programacaoRepositorio.obterPorId(programacaoId);
    }
}
