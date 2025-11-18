package com.astra.cinema.aplicacao.funcionario;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.operacao.ValidacaoIngresso;
import com.astra.cinema.dominio.operacao.ValidacaoIngressoRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.*;

/**
 * Caso de uso para consultar histórico de validações e operações de funcionários.
 */
public class ConsultarHistoricoFuncionarioUseCase {
    private final ValidacaoIngressoRepositorio validacaoIngressoRepositorio;
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public ConsultarHistoricoFuncionarioUseCase(
            ValidacaoIngressoRepositorio validacaoIngressoRepositorio,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        this.validacaoIngressoRepositorio = exigirNaoNulo(validacaoIngressoRepositorio,
            "O repositório de validações não pode ser nulo");
        this.compraRepositorio = exigirNaoNulo(compraRepositorio,
            "O repositório de compras não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio,
            "O repositório de sessões não pode ser nulo");
    }

    /**
     * Lista todas as validações realizadas, ordenadas por data (mais recentes primeiro).
     */
    public List<ItemHistorico> listarTodasValidacoes() {
        List<ValidacaoIngresso> validacoes = validacaoIngressoRepositorio.listarTodas();

        return validacoes.stream()
                .map(this::criarItemHistorico)
                .sorted(Comparator.comparing(ItemHistorico::getDataHora).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Lista todas as validações de um funcionário específico.
     */
    public List<ItemHistorico> listarValidacoesPorFuncionario(FuncionarioId funcionarioId) {
        exigirNaoNulo(funcionarioId, "O ID do funcionário não pode ser nulo");

        List<ValidacaoIngresso> validacoes = validacaoIngressoRepositorio.listarPorFuncionario(funcionarioId);

        return validacoes.stream()
                .map(this::criarItemHistorico)
                .sorted(Comparator.comparing(ItemHistorico::getDataHora).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Lista ingressos ativos (válidos) para possível remarcação.
     */
    public List<IngressoAtivo> listarIngressosAtivos() {
        List<Ingresso> ingressos = compraRepositorio.buscarIngressosAtivos();

        List<IngressoAtivo> result = new ArrayList<>();
        for (Ingresso ingresso : ingressos) {
            Sessao sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
            if (sessao != null) {
                result.add(new IngressoAtivo(
                    ingresso.getIngressoId().getId(),
                    ingresso.getQrCode(),
                    ingresso.getTipo().toString(),
                    ingresso.getStatus().toString(),
                    ingresso.getAssentoId().getValor(),
                    ingresso.getSessaoId().getId(),
                    sessao.getSala(),
                    sessao.getHorario()
                ));
            }
        }

        return result;
    }

    private ItemHistorico criarItemHistorico(ValidacaoIngresso validacao) {
        // Buscar informações adicionais do ingresso
        Ingresso ingresso = compraRepositorio.buscarIngressoPorId(validacao.getIngressoId());

        if (ingresso != null) {
            return new ItemHistorico(
                validacao.getValidacaoId().getValor(),
                validacao.getIngressoId().getId(),
                ingresso.getQrCode(),
                ingresso.getAssentoId().getValor(),
                ingresso.getStatus().toString(),
                ingresso.getSessaoId().getId(),
                validacao.isSucesso(),
                validacao.getMensagem(),
                validacao.getDataHoraValidacao()
            );
        }

        // Fallback se o ingresso não for encontrado
        return new ItemHistorico(
            validacao.getValidacaoId().getValor(),
            validacao.getIngressoId().getId(),
            "N/A",
            "N/A",
            "N/A",
            0,
            validacao.isSucesso(),
            validacao.getMensagem(),
            validacao.getDataHoraValidacao()
        );
    }

    /**
     * Item do histórico de validações.
     */
    public static class ItemHistorico {
        private final Integer validacaoId;
        private final Integer ingressoId;
        private final String qrCode;
        private final String assento;
        private final String status;
        private final Integer sessaoId;
        private final boolean sucesso;
        private final String mensagem;
        private final java.util.Date dataHora;

        public ItemHistorico(Integer validacaoId, Integer ingressoId, String qrCode,
                           String assento, String status, Integer sessaoId,
                           boolean sucesso, String mensagem, java.util.Date dataHora) {
            this.validacaoId = validacaoId;
            this.ingressoId = ingressoId;
            this.qrCode = qrCode;
            this.assento = assento;
            this.status = status;
            this.sessaoId = sessaoId;
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.dataHora = dataHora;
        }

        public Integer getValidacaoId() { return validacaoId; }
        public Integer getIngressoId() { return ingressoId; }
        public String getQrCode() { return qrCode; }
        public String getAssento() { return assento; }
        public String getStatus() { return status; }
        public Integer getSessaoId() { return sessaoId; }
        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }
        public java.util.Date getDataHora() { return dataHora; }
    }

    /**
     * Representa um ingresso ativo para possível remarcação.
     */
    public static class IngressoAtivo {
        private final Integer id;
        private final String qrCode;
        private final String tipo;
        private final String status;
        private final String assento;
        private final Integer sessaoId;
        private final String sala;
        private final java.util.Date horario;

        public IngressoAtivo(Integer id, String qrCode, String tipo, String status,
                           String assento, Integer sessaoId, String sala, java.util.Date horario) {
            this.id = id;
            this.qrCode = qrCode;
            this.tipo = tipo;
            this.status = status;
            this.assento = assento;
            this.sessaoId = sessaoId;
            this.sala = sala;
            this.horario = horario;
        }

        public Integer getId() { return id; }
        public String getQrCode() { return qrCode; }
        public String getTipo() { return tipo; }
        public String getStatus() { return status; }
        public String getAssento() { return assento; }
        public Integer getSessaoId() { return sessaoId; }
        public String getSala() { return sala; }
        public java.util.Date getHorario() { return horario; }
    }
}
