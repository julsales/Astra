package com.astra.cinema.dominio.filme;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.usuario.Funcionario;
import java.util.Date;

/**
 * Service de Filme - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class FilmeService {
    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public FilmeService(FilmeRepositorio filmeRepositorio, SessaoRepositorio sessaoRepositorio) {
        this.filmeRepositorio = exigirNaoNulo(filmeRepositorio, "O repositório de filmes não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
    }

    /**
     * Remove um filme do catálogo.
     * RN6: Um filme só pode ser removido quando não houver sessões futuras vinculadas a ele.
     * RN11: Apenas funcionários com cargo de GERENTE podem gerenciar filmes.
     *
     * @param funcionario Funcionário que está removendo o filme (deve ser GERENTE)
     * @param filmeId ID do filme a ser removido
     */
    public void removerFilme(Funcionario funcionario, FilmeId filmeId) {
        exigirNaoNulo(funcionario, "O funcionário não pode ser nulo");
        exigirEstado(funcionario.isGerente(), "Apenas gerentes podem remover filmes");

        exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        var filme = exigirNaoNulo(filmeRepositorio.obterPorId(filmeId), "Filme não encontrado");

        var sessoes = sessaoRepositorio.buscarPorFilme(filmeId);
        var agora = new Date();

        // RN6: Verifica apenas sessões futuras
        var sessoesFuturas = sessoes.stream()
            .filter(s -> s.getHorario() != null && s.getHorario().after(agora))
            .toList();

        exigirEstado(sessoesFuturas.isEmpty(), "Não é possível remover filme com sessões futuras agendadas");

        filme.remover();
        filmeRepositorio.salvar(filme);
    }

    public void salvar(Filme filme) {
        filmeRepositorio.salvar(exigirNaoNulo(filme, "O filme não pode ser nulo"));
    }

    public Filme obter(FilmeId filmeId) {
        exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        return filmeRepositorio.obterPorId(filmeId);
    }
}
