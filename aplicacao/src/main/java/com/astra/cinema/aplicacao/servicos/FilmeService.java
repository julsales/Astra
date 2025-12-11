package com.astra.cinema.aplicacao.servicos;

import com.astra.cinema.aplicacao.filme.AdicionarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.AlterarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.RemoverFilmeUseCase;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para operações de Filme
 * Centraliza toda lógica de negócio relacionada a filmes
 */
public class FilmeService {

    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final AdicionarFilmeUseCase adicionarFilmeUseCase;
    private final AlterarFilmeUseCase alterarFilmeUseCase;
    private final RemoverFilmeUseCase removerFilmeUseCase;

    public FilmeService(
            FilmeRepositorio filmeRepositorio,
            SessaoRepositorio sessaoRepositorio,
            AdicionarFilmeUseCase adicionarFilmeUseCase,
            AlterarFilmeUseCase alterarFilmeUseCase,
            RemoverFilmeUseCase removerFilmeUseCase) {
        this.filmeRepositorio = filmeRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.adicionarFilmeUseCase = adicionarFilmeUseCase;
        this.alterarFilmeUseCase = alterarFilmeUseCase;
        this.removerFilmeUseCase = removerFilmeUseCase;
    }

    /**
     * Lista filmes com filtros opcionais de status e busca por título
     */
    public List<FilmeDTO> listarFilmes(String status, String busca) {
        List<Filme> filmes;

        if (status != null && !status.isEmpty()) {
            StatusFilme statusFilme = StatusFilme.valueOf(status.toUpperCase());
            if (statusFilme == StatusFilme.EM_CARTAZ) {
                filmes = filmeRepositorio.listarFilmesEmCartaz();
            } else {
                filmes = filmeRepositorio.listarTodos().stream()
                        .filter(f -> f.getStatus() == statusFilme)
                        .collect(Collectors.toList());
            }
        } else {
            filmes = filmeRepositorio.listarTodos();
        }

        // Filtro de busca por título
        if (busca != null && !busca.isEmpty()) {
            String buscaLower = busca.toLowerCase();
            filmes = filmes.stream()
                    .filter(f -> f.getTitulo().toLowerCase().contains(buscaLower))
                    .collect(Collectors.toList());
        }

        return filmes.stream()
                .map(this::mapearFilmeParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista apenas filmes em cartaz
     */
    public List<FilmeDTO> listarFilmesEmCartaz() {
        List<Filme> filmes = filmeRepositorio.listarFilmesEmCartaz();
        return filmes.stream()
                .map(this::mapearFilmeParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtém um filme por ID
     */
    public FilmeDTO obterFilme(Integer id) {
        Filme filme = filmeRepositorio.obterPorId(new FilmeId(id));
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }
        return mapearFilmeParaDTO(filme);
    }

    /**
     * Adiciona um novo filme
     */
    public FilmeDTO adicionarFilme(String titulo, String sinopse, String classificacaoEtaria, Integer duracao, String imagemUrl) {
        Filme filmeSalvo = adicionarFilmeUseCase.executar(titulo, sinopse, classificacaoEtaria, duracao, imagemUrl);
        return mapearFilmeParaDTO(filmeSalvo);
    }

    /**
     * Atualiza um filme existente
     */
    public FilmeDTO atualizarFilme(Integer id, String titulo, String sinopse, String classificacaoEtaria, Integer duracao, String imagemUrl) {
        Filme filmeAtualizado = alterarFilmeUseCase.executar(
                new FilmeId(id),
                titulo,
                sinopse,
                classificacaoEtaria,
                duracao,
                imagemUrl
        );
        return mapearFilmeParaDTO(filmeAtualizado);
    }

    /**
     * Remove um filme
     */
    public void removerFilme(Integer id) {
        removerFilmeUseCase.executar(new FilmeId(id));
    }

    /**
     * Verifica se um filme pode ser removido
     */
    public VerificacaoRemocao verificarPodeRemover(Integer id) {
        List<Sessao> sessoes = sessaoRepositorio.buscarPorFilme(new FilmeId(id));
        boolean podeRemover = sessoes.isEmpty();
        String mensagem = podeRemover ? null : "O filme possui " + sessoes.size() + " sessão(ões) cadastrada(s)";

        return new VerificacaoRemocao(podeRemover, sessoes.size(), mensagem);
    }

    private FilmeDTO mapearFilmeParaDTO(Filme filme) {
        return new FilmeDTO(
                filme.getFilmeId().getId(),
                filme.getTitulo(),
                filme.getSinopse(),
                filme.getClassificacaoEtaria(),
                filme.getDuracao(),
                filme.getImagemUrl(),
                filme.getStatus().name()
        );
    }

    // Classes de resultado
    public record FilmeDTO(
            int id,
            String titulo,
            String sinopse,
            String classificacaoEtaria,
            int duracao,
            String imagemUrl,
            String status
    ) {}

    public record VerificacaoRemocao(boolean podeRemover, int totalSessoes, String mensagem) {}
}
