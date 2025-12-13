package com.astra.cinema.aplicacao.ingresso;

import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

/**
 * Caso de uso: Expirar ingressos de sessões concluídas
 * Responsabilidade: Identificar e expirar ingressos ATIVOS de sessões que já passaram
 */
public class ExpirarIngressosUseCase {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public ExpirarIngressosUseCase(CompraRepositorio compraRepositorio, SessaoRepositorio sessaoRepositorio) {
        this.compraRepositorio = exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
    }

    /**
     * Expira ingressos ativos de uma sessão específica que já passou
     * @param sessaoId ID da sessão
     * @return quantidade de ingressos expirados
     */
    public int executarParaSessao(SessaoId sessaoId) {
        exigirNaoNulo(sessaoId, "O ID da sessão não pode ser nulo");

        Sessao sessao = sessaoRepositorio.obterPorId(sessaoId);
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada");
        }

        // Verifica se a sessão já passou
        Date agora = new Date();
        if (sessao.getHorario().after(agora)) {
            return 0; // Sessão ainda não aconteceu
        }

        // Busca todos os ingressos ativos
        List<Ingresso> ingressosAtivos = compraRepositorio.buscarIngressosAtivos();

        // Filtra ingressos ativos da sessão
        List<Ingresso> ingressosParaExpirar = ingressosAtivos.stream()
            .filter(ing -> ing.getSessaoId().equals(sessaoId))
            .filter(ing -> ing.getStatus() == StatusIngresso.ATIVO)
            .collect(Collectors.toList());

        // Expira cada ingresso
        for (Ingresso ingresso : ingressosParaExpirar) {
            ingresso.expirar();
            compraRepositorio.atualizarIngresso(ingresso);
        }

        return ingressosParaExpirar.size();
    }

    /**
     * Expira ingressos ativos de todas as sessões que já passaram
     * @return quantidade total de ingressos expirados
     */
    public int executarParaTodasSessoes() {
        Date agora = new Date();
        int totalExpirados = 0;

        // Busca todas as sessões
        List<Sessao> todasSessoes = sessaoRepositorio.listarTodas();

        // Filtra sessões que já passaram e não estão canceladas
        List<Sessao> sessoesPassadas = todasSessoes.stream()
            .filter(s -> s.getHorario().before(agora))
            .filter(s -> s.getStatus() != StatusSessao.CANCELADA)
            .collect(Collectors.toList());

        // Expira ingressos de cada sessão
        for (Sessao sessao : sessoesPassadas) {
            totalExpirados += executarParaSessao(sessao.getSessaoId());
        }

        return totalExpirados;
    }

    /**
     * Resultado da expiração de ingressos
     */
    public record ResultadoExpiracao(
        int ingressosExpirados,
        String mensagem
    ) {}
}
