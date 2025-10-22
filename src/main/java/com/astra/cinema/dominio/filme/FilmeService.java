package com.astra.cinema.dominio.filme;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import java.util.List;

public class FilmeService {
    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public FilmeService(FilmeRepositorio filmeRepositorio, SessaoRepositorio sessaoRepositorio) {
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        
        this.filmeRepositorio = filmeRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public void removerFilme(FilmeId filmeId) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        
        var sessoes = sessaoRepositorio.buscarPorFilme(filmeId);
        if (!sessoes.isEmpty()) {
            throw new IllegalStateException("Não é possível remover o filme pois há sessões ativas");
        }
        
        var filme = filmeRepositorio.obterPorId(filmeId);
        filme.remover();
        filmeRepositorio.salvar(filme);
    }

    public void salvar(Filme filme) {
        filmeRepositorio.salvar(filme);
    }

    public Filme obter(FilmeId filmeId) {
        return filmeRepositorio.obterPorId(filmeId);
    }
}
