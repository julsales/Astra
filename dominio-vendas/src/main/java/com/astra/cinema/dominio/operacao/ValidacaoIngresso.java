package com.astra.cinema.dominio.operacao;

import com.astra.cinema.dominio.comum.*;

import java.util.Date;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.*;

/**
 * Representa uma validação de ingresso realizada por um funcionário.
 * Esta entidade registra o histórico de validações para auditoria e controle.
 */
public class ValidacaoIngresso {
    private final ValidacaoIngressoId validacaoId;
    private final IngressoId ingressoId;
    private final FuncionarioId funcionarioId;
    private final Date dataHoraValidacao;
    private final boolean sucesso;
    private final String mensagem;

    public ValidacaoIngresso(ValidacaoIngressoId validacaoId, IngressoId ingressoId,
                            FuncionarioId funcionarioId, Date dataHoraValidacao,
                            boolean sucesso, String mensagem) {
        this.validacaoId = validacaoId; // Pode ser null antes de persistir
        this.ingressoId = exigirNaoNulo(ingressoId, "O ID do ingresso não pode ser nulo");
        this.funcionarioId = exigirNaoNulo(funcionarioId, "O ID do funcionário não pode ser nulo");
        this.dataHoraValidacao = exigirNaoNulo(dataHoraValidacao, "A data/hora de validação não pode ser nula");
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public ValidacaoIngressoId getValidacaoId() {
        return validacaoId;
    }

    public IngressoId getIngressoId() {
        return ingressoId;
    }

    public FuncionarioId getFuncionarioId() {
        return funcionarioId;
    }

    public Date getDataHoraValidacao() {
        return dataHoraValidacao;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }
}
