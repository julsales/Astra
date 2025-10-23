package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import java.util.Date;
import java.util.Map;

/**
 * Service de Sessão - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class SessaoService {
    private final SessaoRepositorio sessaoRepositorio;
    private final CriarSessaoUseCase criarSessaoUseCase;

    public SessaoService(SessaoRepositorio sessaoRepositorio, FilmeRepositorio filmeRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        
        this.sessaoRepositorio = sessaoRepositorio;
        this.criarSessaoUseCase = new CriarSessaoUseCase(sessaoRepositorio, filmeRepositorio);
    }

    public Sessao criarSessao(FilmeId filmeId, Date horario, Map<AssentoId, Boolean> assentos) {
        return criarSessaoUseCase.executar(filmeId, horario, assentos);
    }

    public void salvar(Sessao sessao) {
        sessaoRepositorio.salvar(sessao);
    }

    public Sessao obter(SessaoId sessaoId) {
        return sessaoRepositorio.obterPorId(sessaoId);
    }
}
