package com.astra.cinema.dominio.programacao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirColecaoNaoVazia;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;
import java.util.Date;
import java.util.List;

/**
 * Service de Programação - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class ProgramacaoService {
    private final ProgramacaoRepositorio programacaoRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public ProgramacaoService(ProgramacaoRepositorio programacaoRepositorio, 
                             SessaoRepositorio sessaoRepositorio) {
        this.programacaoRepositorio = exigirNaoNulo(programacaoRepositorio,
            "O repositório de programações não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
    }

    public Programacao criarProgramacao(Date periodoInicio, Date periodoFim, List<SessaoId> sessoes) {
        Date inicio = exigirNaoNulo(periodoInicio, "Período inicial não pode ser nulo");
        Date fim = exigirNaoNulo(periodoFim, "Período final não pode ser nulo");
        exigirEstado(!inicio.after(fim), "Data inicial não pode ser posterior à final");

        var sessoesRequisitadas = exigirColecaoNaoVazia(sessoes, "A programação deve ter sessões");
        sessoesRequisitadas.forEach(sessaoId -> {
            var sessao = exigirNaoNulo(sessaoRepositorio.obterPorId(sessaoId), "Sessão inválida: " + sessaoId);
            exigirEstado(sessao.getStatus() == StatusSessao.DISPONIVEL,
                "Sessão indisponível para programação: " + sessaoId);
        });

        var programacao = new Programacao(new ProgramacaoId(), inicio, fim, sessoesRequisitadas);
        programacaoRepositorio.salvar(programacao);
        return programacao;
    }

    public void salvar(Programacao programacao) {
        programacaoRepositorio.salvar(exigirNaoNulo(programacao, "A programação não pode ser nula"));
    }

    public Programacao obter(ProgramacaoId programacaoId) {
        exigirNaoNulo(programacaoId, "O id da programação não pode ser nulo");
        return programacaoRepositorio.obterPorId(programacaoId);
    }
}
