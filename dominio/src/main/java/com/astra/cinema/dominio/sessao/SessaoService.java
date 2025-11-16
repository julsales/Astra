package com.astra.cinema.dominio.sessao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import java.util.Date;
import java.util.Map;

/**
 * Service de Sessão - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class SessaoService {
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public SessaoService(SessaoRepositorio sessaoRepositorio, FilmeRepositorio filmeRepositorio) {
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
        this.filmeRepositorio = exigirNaoNulo(filmeRepositorio, "O repositório de filmes não pode ser nulo");
    }

    public Sessao criarSessao(FilmeId filmeId, Date horario, Map<AssentoId, Boolean> assentos) {
        exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        exigirNaoNulo(horario, "O horário não pode ser nulo");
        var mapaAssentos = exigirNaoNulo(assentos, "O mapa de assentos não pode ser nulo");

    var filme = exigirNaoNulo(filmeRepositorio.obterPorId(filmeId), "Filme não encontrado");
    exigirEstado(filme.getStatus() == StatusFilme.EM_CARTAZ, "Filme não está em cartaz");

    var sessao = new Sessao(null, filmeId, horario, StatusSessao.DISPONIVEL, mapaAssentos);
        sessaoRepositorio.salvar(sessao);
        return sessao;
    }

    public void salvar(Sessao sessao) {
        sessaoRepositorio.salvar(exigirNaoNulo(sessao, "A sessão não pode ser nula"));
    }

    public Sessao obter(SessaoId sessaoId) {
        exigirNaoNulo(sessaoId, "O id da sessão não pode ser nulo");
        return sessaoRepositorio.obterPorId(sessaoId);
    }
}
