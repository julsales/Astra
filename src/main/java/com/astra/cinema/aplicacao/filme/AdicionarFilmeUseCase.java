package com.astra.cinema.aplicacao.filme;

import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;

/**
 * Caso de uso: Adicionar um novo filme ao catálogo
 * Responsabilidade: Orquestrar a criação de um novo filme
 * 
 * Padrão: Command (encapsula a operação de adicionar filme)
 */
public class AdicionarFilmeUseCase {
    private final FilmeRepositorio filmeRepositorio;

    public AdicionarFilmeUseCase(FilmeRepositorio filmeRepositorio) {
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        this.filmeRepositorio = filmeRepositorio;
    }

    /**
     * Adiciona um novo filme ao catálogo
     * 
     * @param titulo Título do filme
     * @param sinopse Sinopse do filme
     * @param classificacaoEtaria Classificação etária
     * @param duracao Duração em minutos
     * @return Filme criado
     */
    public Filme executar(String titulo, String sinopse, String classificacaoEtaria, int duracao) {
        // Validações
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título do filme não pode ser vazio");
        }
        if (duracao <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero");
        }
        if (classificacaoEtaria == null || classificacaoEtaria.isBlank()) {
            throw new IllegalArgumentException("A classificação etária não pode ser vazia");
        }

        // Cria o filme com status EM_CARTAZ (sem ID - será gerado pelo banco)
        Filme novoFilme = new Filme(
            null,  // ID será gerado automaticamente pelo banco via IDENTITY
            titulo,
            sinopse != null ? sinopse : "",
            classificacaoEtaria,
            duracao,
            StatusFilme.EM_CARTAZ
        );

        // Persiste e retorna o filme com ID gerado pelo banco
        return filmeRepositorio.salvar(novoFilme);
    }
}
