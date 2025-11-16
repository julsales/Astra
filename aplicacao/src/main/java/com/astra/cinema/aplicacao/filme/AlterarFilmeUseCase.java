package com.astra.cinema.aplicacao.filme;

import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;

/**
 * Caso de uso: Alterar dados de um filme existente
 * Responsabilidade: Orquestrar a atualização dos dados do filme
 * 
 * Padrão: Command (encapsula a operação de alteração)
 */
public class AlterarFilmeUseCase {
    private final FilmeRepositorio filmeRepositorio;

    public AlterarFilmeUseCase(FilmeRepositorio filmeRepositorio) {
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        this.filmeRepositorio = filmeRepositorio;
    }

    /**
     * Altera os dados de um filme
     * 
     * @param filmeId ID do filme
     * @param novoTitulo Novo título (null mantém o atual)
     * @param novaSinopse Nova sinopse (null mantém a atual)
     * @param novaClassificacao Nova classificação (null mantém a atual)
     * @param novaDuracao Nova duração (0 ou negativo mantém a atual)
     * @return Filme atualizado
     */
    public Filme executar(FilmeId filmeId, String novoTitulo, String novaSinopse, 
                          String novaClassificacao, int novaDuracao) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O ID do filme não pode ser nulo");
        }

        // Busca o filme
        Filme filme = filmeRepositorio.obterPorId(filmeId);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }

        // Valida novos dados
        if (novoTitulo != null && !novoTitulo.isBlank()) {
            validarTitulo(novoTitulo);
        }
        if (novaDuracao > 0) {
            validarDuracao(novaDuracao);
        }

        // Cria novo filme com dados atualizados (imutabilidade)
        Filme filmeAtualizado = new Filme(
            filme.getFilmeId(),
            novoTitulo != null && !novoTitulo.isBlank() ? novoTitulo : filme.getTitulo(),
            novaSinopse != null ? novaSinopse : filme.getSinopse(),
            novaClassificacao != null && !novaClassificacao.isBlank() ? novaClassificacao : filme.getClassificacaoEtaria(),
            novaDuracao > 0 ? novaDuracao : filme.getDuracao(),
            filme.getStatus()
        );

        // Persiste e retorna o filme atualizado
        return filmeRepositorio.salvar(filmeAtualizado);
    }

    private void validarTitulo(String titulo) {
        if (titulo.length() < 2) {
            throw new IllegalArgumentException("O título deve ter pelo menos 2 caracteres");
        }
    }

    private void validarDuracao(int duracao) {
        if (duracao <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero");
        }
        if (duracao > 600) { // 10 horas
            throw new IllegalArgumentException("A duração não pode exceder 600 minutos");
        }
    }
}
