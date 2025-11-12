package com.astra.cinema.aplicacao.filme;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.Date;

/**
 * Caso de uso: Remover um filme do catálogo
 * Responsabilidade: Orquestrar a remoção validando se há sessões ativas
 * 
 * Padrão: Strategy (através da interface FilmeRepositorio)
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

    /**
     * Remove um filme do catálogo alterando seu status para RETIRADO
     * 
     * @param filmeId ID do filme a ser removido
     * @throws IllegalStateException se houver sessões futuras para o filme
     * @throws IllegalArgumentException se o filme não for encontrado
     */
    public void executar(FilmeId filmeId) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }

        // Busca o filme
        var filme = filmeRepositorio.obterPorId(filmeId);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }

        // Verifica se há sessões futuras
        Date agora = new Date();
        var sessoesFuturas = sessaoRepositorio.buscarPorFilme(filmeId).stream()
                .filter(sessao -> sessao.getHorario().after(agora))
                .toList();

        if (!sessoesFuturas.isEmpty()) {
            throw new IllegalStateException("Não é possível remover o filme. Há " + sessoesFuturas.size() + " sessões futuras agendadas");
        }

        // Remove o filme (altera status para RETIRADO)
        filme.retirarDeCartaz();
        filmeRepositorio.salvar(filme);
    }

    /**
     * Verifica se um filme pode ser removido
     * 
     * @param filmeId ID do filme
     * @return true se o filme pode ser removido
     */
    public boolean podeRemover(FilmeId filmeId) {
        if (filmeId == null) {
            return false;
        }

        var filme = filmeRepositorio.obterPorId(filmeId);
        if (filme == null) {
            return false;
        }

        Date agora = new Date();
        return sessaoRepositorio.buscarPorFilme(filmeId).stream()
                .noneMatch(sessao -> sessao.getHorario().after(agora));
    }
}
