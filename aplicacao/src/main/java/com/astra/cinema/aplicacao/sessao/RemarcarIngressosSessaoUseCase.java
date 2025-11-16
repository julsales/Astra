package com.astra.cinema.aplicacao.sessao;

import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso de uso: Remarcar ingressos de uma sessão quando ocorre problema técnico.
 * Permite movimentar ingressos em massa ou individualmente para um novo horário.
 */
public class RemarcarIngressosSessaoUseCase {

    private final SessaoRepositorio sessaoRepositorio;

    public RemarcarIngressosSessaoUseCase(SessaoRepositorio sessaoRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public RemarcacaoResultado executar(
            SessaoId sessaoId,
            Date novoHorario,
            EstrategiaRemarcacao estrategia,
            List<String> assentosAfetados) {

        if (sessaoId == null) {
            throw new IllegalArgumentException("O ID da sessão não pode ser nulo");
        }
        if (novoHorario == null) {
            throw new IllegalArgumentException("O novo horário não pode ser nulo");
        }
        if (estrategia == null) {
            throw new IllegalArgumentException("A estratégia de remarcação é obrigatória");
        }

        Sessao sessao = sessaoRepositorio.obterPorId(sessaoId);
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada");
        }

        if (sessao.getStatus() == StatusSessao.CANCELADA) {
            throw new IllegalStateException("Não é possível remarcar uma sessão cancelada");
        }

        Date agora = new Date();
        if (novoHorario.before(agora)) {
            throw new IllegalArgumentException("O novo horário precisa ser no futuro");
        }

        Map<AssentoId, Boolean> mapaAssentos = sessao.getMapaAssentosDisponiveis();
        int reservasAtivas = (int) mapaAssentos.values().stream()
                .filter(disponivel -> !disponivel)
                .count();

        int ingressosRemarcados;
        if (estrategia == EstrategiaRemarcacao.MASSA) {
            ingressosRemarcados = reservasAtivas;
        } else {
            if (assentosAfetados == null || assentosAfetados.isEmpty()) {
                throw new IllegalArgumentException("Informe os assentos que serão remarcados individualmente");
            }

            Set<AssentoId> idsSolicitados = converterAssentos(assentosAfetados);
            for (AssentoId assentoId : idsSolicitados) {
                if (!mapaAssentos.containsKey(assentoId)) {
                    throw new IllegalArgumentException("Assento " + assentoId.getValor() + " não pertence à sessão");
                }
            }

            ingressosRemarcados = (int) idsSolicitados.stream()
                    .filter(id -> Boolean.FALSE.equals(mapaAssentos.get(id)))
                    .count();
        }

        Sessao sessaoAtualizada = new Sessao(
            sessao.getSessaoId(),
            sessao.getFilmeId(),
            novoHorario,
            sessao.getStatus(),
            mapaAssentos,
            sessao.getSala(),
            sessao.getCapacidade()
        );

        sessaoRepositorio.salvar(sessaoAtualizada);

        return new RemarcacaoResultado(ingressosRemarcados, reservasAtivas, novoHorario, estrategia);
    }

    private Set<AssentoId> converterAssentos(List<String> assentos) {
        if (assentos == null) {
            return Collections.emptySet();
        }
        return assentos.stream()
                .filter(item -> item != null && !item.isBlank())
                .map(item -> new AssentoId(item.trim().toUpperCase()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public enum EstrategiaRemarcacao {
        MASSA,
        INDIVIDUAL
    }

    public record RemarcacaoResultado(
        int ingressosRemarcados,
        int ingressosReservados,
        Date novoHorario,
        EstrategiaRemarcacao estrategia
    ) {}
}
