package com.astra.cinema.dominio.filme;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

/**
 * Service de Filme - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class FilmeService {
    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public FilmeService(FilmeRepositorio filmeRepositorio, SessaoRepositorio sessaoRepositorio) {
        this.filmeRepositorio = exigirNaoNulo(filmeRepositorio, "O repositório de filmes não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
    }

    public void removerFilme(FilmeId filmeId) {
        exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        var filme = exigirNaoNulo(filmeRepositorio.obterPorId(filmeId), "Filme não encontrado");

        var sessoes = sessaoRepositorio.buscarPorFilme(filmeId);
        exigirEstado(sessoes.isEmpty(), "Não é possível remover filme com sessões ativas");

        filme.remover();
        filmeRepositorio.salvar(filme);
    }

    public void salvar(Filme filme) {
        filmeRepositorio.salvar(exigirNaoNulo(filme, "O filme não pode ser nulo"));
    }

    public Filme obter(FilmeId filmeId) {
        exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        return filmeRepositorio.obterPorId(filmeId);
    }
}
