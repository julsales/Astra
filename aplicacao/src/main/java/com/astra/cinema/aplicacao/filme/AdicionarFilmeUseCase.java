package com.astra.cinema.aplicacao.filme;

import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;

import java.net.URI;

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
     * @param imagemUrl URL da imagem (opcional)
     * @param status Status inicial do filme (EM_CARTAZ, EM_BREVE, RETIRADO)
     * @return Filme criado
     */
    public Filme executar(String titulo, String sinopse, String classificacaoEtaria, int duracao, String imagemUrl, StatusFilme status) {
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
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }

        // Cria o filme com o status informado (sem ID - será gerado pelo banco)
        Filme novoFilme = new Filme(
            null,  // ID será gerado automaticamente pelo banco via IDENTITY
            titulo,
            sinopse != null ? sinopse : "",
            classificacaoEtaria,
            duracao,
            validarImagem(imagemUrl),
            status
        );

        // Persiste e retorna o filme com ID gerado pelo banco
        return filmeRepositorio.salvar(novoFilme);
    }

    private String validarImagem(String imagemUrl) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return null;
        }

        String valorNormalizado = imagemUrl.trim();
        try {
            URI uri = URI.create(valorNormalizado);
            String esquema = uri.getScheme();
            if (esquema == null ||
                (!"http".equalsIgnoreCase(esquema) && !"https".equalsIgnoreCase(esquema))) {
                throw new IllegalArgumentException("A URL da imagem deve usar HTTP ou HTTPS");
            }
            return valorNormalizado;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("URL de imagem inválida", e);
        }
    }
}
