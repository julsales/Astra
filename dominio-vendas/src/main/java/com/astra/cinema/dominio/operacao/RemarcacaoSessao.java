package com.astra.cinema.dominio.operacao;

import com.astra.cinema.dominio.comum.*;

import java.util.Date;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.*;

/**
 * Representa uma remarcação de sessão realizada por um funcionário.
 * Esta entidade registra o histórico de remarcações para auditoria e controle.
 *
 * Regra de Negócio: Deve registrar o motivo técnico da remarcação.
 * Clientes com contas devem ser notificados automaticamente.
 */
public class RemarcacaoSessao {
    private final RemarcacaoId remarcacaoId;
    private final IngressoId ingressoId;
    private final SessaoId sessaoOriginal;
    private final SessaoId sessaoNova;
    private final AssentoId assentoOriginal;
    private final AssentoId assentoNovo;
    private final FuncionarioId funcionarioId;
    private final Date dataHoraRemarcacao;
    private final String motivoTecnico;

    public RemarcacaoSessao(RemarcacaoId remarcacaoId, IngressoId ingressoId,
                           SessaoId sessaoOriginal, SessaoId sessaoNova,
                           AssentoId assentoOriginal, AssentoId assentoNovo,
                           FuncionarioId funcionarioId, Date dataHoraRemarcacao,
                           String motivoTecnico) {
        this.remarcacaoId = remarcacaoId; // Pode ser null antes de persistir
        this.ingressoId = exigirNaoNulo(ingressoId, "O ID do ingresso não pode ser nulo");
        this.sessaoOriginal = exigirNaoNulo(sessaoOriginal, "A sessão original não pode ser nula");
        this.sessaoNova = exigirNaoNulo(sessaoNova, "A sessão nova não pode ser nula");
        this.assentoOriginal = assentoOriginal;
        this.assentoNovo = assentoNovo;
        this.funcionarioId = exigirNaoNulo(funcionarioId, "O ID do funcionário não pode ser nulo");
        this.dataHoraRemarcacao = exigirNaoNulo(dataHoraRemarcacao, "A data/hora de remarcação não pode ser nula");
        this.motivoTecnico = exigirTexto(motivoTecnico, "O motivo técnico da remarcação é obrigatório");
    }

    public RemarcacaoId getRemarcacaoId() {
        return remarcacaoId;
    }

    public IngressoId getIngressoId() {
        return ingressoId;
    }

    public SessaoId getSessaoOriginal() {
        return sessaoOriginal;
    }

    public SessaoId getSessaoNova() {
        return sessaoNova;
    }

    public AssentoId getAssentoOriginal() {
        return assentoOriginal;
    }

    public AssentoId getAssentoNovo() {
        return assentoNovo;
    }

    public FuncionarioId getFuncionarioId() {
        return funcionarioId;
    }

    public Date getDataHoraRemarcacao() {
        return dataHoraRemarcacao;
    }

    public String getMotivoTecnico() {
        return motivoTecnico;
    }
}
