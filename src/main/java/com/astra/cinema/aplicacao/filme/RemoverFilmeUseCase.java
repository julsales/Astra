package com.astra.cinema.aplicacao.filme;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

/**
 * Caso de uso: Remover um filme do catálogo
 * Responsabilidade: Orquestrar a remoção validando se há sessões ativas
 */
public class RemoverFilmeUseCase {
    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public RemoverFilmeUseCase(FilmeRepositorio filmeRepositorio,
                               SessaoRepositorio sessaoRepositorio) {
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        
        this.filmeRepositorio = filmeRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public void executar(FilmeId filmeId) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        
        var sessoes = sessaoRepositorio.buscarPorFilme(filmeId);
        if (!sessoes.isEmpty()) {
            throw new IllegalStateException("Não é possível remover um filme, há sessões ativas");
        }
        
        var filme = filmeRepositorio.obterPorId(filmeId);
        filme.retirarDeCartaz();
        filmeRepositorio.salvar(filme);
    }
}
