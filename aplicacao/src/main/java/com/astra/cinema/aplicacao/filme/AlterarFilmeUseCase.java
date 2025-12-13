package com.astra.cinema.aplicacao.filme;

import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Caso de uso: Alterar dados de um filme existente
 * Responsabilidade: Orquestrar a atualização dos dados do filme
 * 
 * Padrão: Command (encapsula a operação de alteração)
 */
public class AlterarFilmeUseCase {
    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public AlterarFilmeUseCase(FilmeRepositorio filmeRepositorio, SessaoRepositorio sessaoRepositorio) {
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
     * Altera os dados de um filme
     * 
     * @param filmeId ID do filme
     * @param novoTitulo Novo título (null mantém o atual)
     * @param novaSinopse Nova sinopse (null mantém a atual)
     * @param novaClassificacao Nova classificação (null mantém a atual)
     * @param novaDuracao Nova duração (0 ou negativo mantém a atual)
    * @param novaImagemUrl Nova URL da imagem (null mantém a atual)
    * @param novoStatus Novo status (null mantém o atual)
    * @return Filme atualizado
    */
    public Filme executar(FilmeId filmeId, String novoTitulo, String novaSinopse, 
                     String novaClassificacao, int novaDuracao, String novaImagemUrl, 
                     com.astra.cinema.dominio.filme.StatusFilme novoStatus) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O ID do filme não pode ser nulo");
        }

        // Busca o filme
        Filme filme = filmeRepositorio.obterPorId(filmeId);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }

        // Valida alteração de status
        if (novoStatus != null && novoStatus != filme.getStatus()) {
            validarAlteracaoStatus(filmeId, filme.getStatus(), novoStatus);
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
            novaImagemUrl != null ? validarImagem(novaImagemUrl) : filme.getImagemUrl(),
            novoStatus != null ? novoStatus : filme.getStatus()
        );

        // Persiste e retorna o filme atualizado
        return filmeRepositorio.salvar(filmeAtualizado);
    }

    private void validarAlteracaoStatus(FilmeId filmeId, 
                                       com.astra.cinema.dominio.filme.StatusFilme statusAtual,
                                       com.astra.cinema.dominio.filme.StatusFilme novoStatus) {
        // Verifica se há sessões futuras para o filme
        Date agora = new Date();
        List<Sessao> sessoesFuturas = sessaoRepositorio.buscarPorFilme(filmeId).stream()
                .filter(sessao -> sessao.getHorario().after(agora))
                .toList();
        
        if (!sessoesFuturas.isEmpty()) {
            throw new IllegalStateException(
                "Não é possível alterar o status do filme. " +
                "Há " + sessoesFuturas.size() + " sessão(ões) futura(s) agendada(s). " +
                "Cancele ou remova as sessões antes de alterar o status."
            );
        }
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

    private String validarImagem(String imagemUrl) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return null;
        }

        String valor = imagemUrl.trim();
        try {
            URI uri = URI.create(valor);
            String esquema = uri.getScheme();
            if (esquema == null || (!"http".equalsIgnoreCase(esquema) && !"https".equalsIgnoreCase(esquema))) {
                throw new IllegalArgumentException("A URL da imagem deve usar HTTP ou HTTPS");
            }
            return valor;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("URL de imagem inválida", e);
        }
    }
}
