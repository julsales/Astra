package com.astra.cinema.dominio.sessao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import com.astra.cinema.dominio.usuario.Funcionario;
import java.util.Date;
import java.util.Map;

/**
 * Service de Sessão - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class SessaoService {
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public SessaoService(SessaoRepositorio sessaoRepositorio, FilmeRepositorio filmeRepositorio) {
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
        this.filmeRepositorio = exigirNaoNulo(filmeRepositorio, "O repositório de filmes não pode ser nulo");
    }

    /**
     * Cria uma nova sessão para um filme.
     * RN4: Uma sessão só pode ser criada para filmes com status "EM_CARTAZ".
     * RN11: Apenas funcionários com cargo de GERENTE podem gerenciar sessões.
     *
     * @param funcionario Funcionário que está criando a sessão (deve ser GERENTE)
     * @param filmeId ID do filme
     * @param horario Horário da sessão
     * @param assentos Mapa de assentos disponíveis
     * @return Sessão criada
     */
    public Sessao criarSessao(Funcionario funcionario, FilmeId filmeId, Date horario, Map<AssentoId, Boolean> assentos) {
        exigirNaoNulo(funcionario, "O funcionário não pode ser nulo");
        exigirEstado(funcionario.isGerente(), "Apenas gerentes podem criar sessões");

        exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        exigirNaoNulo(horario, "O horário não pode ser nulo");
        var mapaAssentos = exigirNaoNulo(assentos, "O mapa de assentos não pode ser nulo");

        var filme = exigirNaoNulo(filmeRepositorio.obterPorId(filmeId), "Filme não encontrado");
        exigirEstado(filme.getStatus() == StatusFilme.EM_CARTAZ, "Filme não está em cartaz");

        var sessao = new Sessao(null, filmeId, horario, StatusSessao.DISPONIVEL, mapaAssentos, "Sala 1", mapaAssentos.size());
        sessaoRepositorio.salvar(sessao);
        return sessao;
    }

    public void salvar(Sessao sessao) {
        sessaoRepositorio.salvar(exigirNaoNulo(sessao, "A sessão não pode ser nula"));
    }

    public Sessao obter(SessaoId sessaoId) {
        exigirNaoNulo(sessaoId, "O id da sessão não pode ser nulo");
        return sessaoRepositorio.obterPorId(sessaoId);
    }
}
