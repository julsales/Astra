package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import java.util.Date;
import java.util.Map;

public class SessaoService {
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public SessaoService(SessaoRepositorio sessaoRepositorio, FilmeRepositorio filmeRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    public Sessao criarSessao(FilmeId filmeId, Date horario, Map<AssentoId, Boolean> assentos) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        
        var filme = filmeRepositorio.obterPorId(filmeId);
        if (filme.getStatus() != StatusFilme.EM_CARTAZ) {
            throw new IllegalStateException("O filme não está em cartaz");
        }
        
        var sessaoId = new SessaoId(System.identityHashCode(filmeId)); // Simplificado
        var sessao = new Sessao(sessaoId, filmeId, horario, StatusSessao.DISPONIVEL, assentos);
        sessaoRepositorio.salvar(sessao);
        
        return sessao;
    }

    public void salvar(Sessao sessao) {
        sessaoRepositorio.salvar(sessao);
    }

    public Sessao obter(SessaoId sessaoId) {
        return sessaoRepositorio.obterPorId(sessaoId);
    }
}
