package com.astra.cinema.aplicacao.sessao;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import java.util.Date;
import java.util.Map;

/**
 * Caso de uso: Criar uma nova sessão de cinema
 * Responsabilidade: Orquestrar a criação de sessão validando o filme
 */
public class CriarSessaoUseCase {
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public CriarSessaoUseCase(SessaoRepositorio sessaoRepositorio,
                              FilmeRepositorio filmeRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    public Sessao executar(FilmeId filmeId, Date horario, Map<AssentoId, Boolean> assentos) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        
        var filme = filmeRepositorio.obterPorId(filmeId);
        if (filme.getStatus() != StatusFilme.EM_CARTAZ) {
            throw new IllegalStateException("O filme não está em cartaz");
        }
        
        var sessaoId = new SessaoId(System.identityHashCode(horario));
        var sessao = new Sessao(sessaoId, filmeId, horario, StatusSessao.DISPONIVEL, assentos);
        sessaoRepositorio.salvar(sessao);
        
        return sessao;
    }
}
