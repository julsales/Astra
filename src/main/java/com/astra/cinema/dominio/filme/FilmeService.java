package com.astra.cinema.dominio.filme;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.aplicacao.filme.RemoverFilmeUseCase;

/**
 * Service de Filme - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class FilmeService {
    private final FilmeRepositorio filmeRepositorio;
    private final RemoverFilmeUseCase removerFilmeUseCase;

    public FilmeService(FilmeRepositorio filmeRepositorio, SessaoRepositorio sessaoRepositorio) {
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        
        this.filmeRepositorio = filmeRepositorio;
        this.removerFilmeUseCase = new RemoverFilmeUseCase(filmeRepositorio, sessaoRepositorio);
    }

    public void removerFilme(FilmeId filmeId) {
        removerFilmeUseCase.executar(filmeId);
    }

    public void salvar(Filme filme) {
        filmeRepositorio.salvar(filme);
    }

    public Filme obter(FilmeId filmeId) {
        return filmeRepositorio.obterPorId(filmeId);
    }
}
