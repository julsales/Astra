package com.astra.cinema.aplicacao.sessao;

import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;

import java.util.Date;
import java.util.List;

/**
 * Caso de uso: Remover (cancelar) uma sessão
 * Responsabilidade: Orquestrar o cancelamento de sessão
 *
 * Padrão: Command (encapsula a operação de remoção)
 */
public class RemoverSessaoUseCase {
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public RemoverSessaoUseCase(SessaoRepositorio sessaoRepositorio, FilmeRepositorio filmeRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    /**
     * Remove (cancela) uma sessão
     * 
     * @param sessaoId ID da sessão
     * @throws IllegalStateException se a sessão já passou ou já está cancelada
     */
    public void executar(SessaoId sessaoId) {
        if (sessaoId == null) {
            throw new IllegalArgumentException("O ID da sessão não pode ser nulo");
        }

        // Busca a sessão
        Sessao sessao = sessaoRepositorio.obterPorId(sessaoId);
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada");
        }

        // Verifica se já está cancelada
        if (sessao.getStatus() == StatusSessao.CANCELADA) {
            throw new IllegalStateException("A sessão já está cancelada");
        }

        // Verifica se a sessão já passou
        Date agora = new Date();
        if (sessao.getHorario().before(agora)) {
            throw new IllegalStateException("Não é possível cancelar uma sessão que já passou");
        }

        // Cancela a sessão (cria nova instância)
        Sessao sessaoCancelada = new Sessao(
            sessao.getSessaoId(),
            sessao.getFilmeId(),
            sessao.getHorario(),
            StatusSessao.CANCELADA,
            sessao.getMapaAssentosDisponiveis(),
            sessao.getSalaId()
        );

        // Persiste
        sessaoRepositorio.salvar(sessaoCancelada);

        // Verifica se todas as sessões do filme foram canceladas
        verificarEAtualizarStatusFilme(sessao.getFilmeId());
    }

    /**
     * Verifica se todas as sessões de um filme foram canceladas e atualiza o status do filme se necessário
     */
    private void verificarEAtualizarStatusFilme(FilmeId filmeId) {
        // Buscar todas as sessões do filme
        List<Sessao> sessoesDoFilme = sessaoRepositorio.buscarPorFilme(filmeId);

        // Verificar se todas as sessões estão canceladas
        boolean todasCanceladas = sessoesDoFilme.stream()
                .allMatch(s -> s.getStatus() == StatusSessao.CANCELADA);

        // Se todas as sessões foram canceladas e o filme está EM_CARTAZ, mudar para RETIRADO
        if (todasCanceladas && !sessoesDoFilme.isEmpty()) {
            Filme filme = filmeRepositorio.obterPorId(filmeId);
            if (filme != null && filme.getStatus() == StatusFilme.EM_CARTAZ) {
                // Criar nova instância do filme com status RETIRADO
                Filme filmeRetirado = new Filme(
                    filme.getFilmeId(),
                    filme.getTitulo(),
                    filme.getSinopse(),
                    filme.getClassificacaoEtaria(),
                    filme.getDuracao(),
                    filme.getImagemUrl(),
                    StatusFilme.RETIRADO
                );
                filmeRepositorio.salvar(filmeRetirado);
            }
        }
    }

    /**
     * Verifica se uma sessão pode ser removida
     * 
     * @param sessaoId ID da sessão
     * @return true se pode ser removida
     */
    public boolean podeRemover(SessaoId sessaoId) {
        if (sessaoId == null) {
            return false;
        }

        Sessao sessao = sessaoRepositorio.obterPorId(sessaoId);
        if (sessao == null) {
            return false;
        }

        if (sessao.getStatus() == StatusSessao.CANCELADA) {
            return false;
        }

        Date agora = new Date();
        return sessao.getHorario().after(agora);
    }
}
