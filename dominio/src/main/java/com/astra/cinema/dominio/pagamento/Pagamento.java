package com.astra.cinema.dominio.pagamento;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.PagamentoId;
import java.util.Date;

public class Pagamento implements Cloneable {
    private final PagamentoId pagamentoId;
    private double valor;
    private StatusPagamento status;
    private Date dataPagamento;

    public Pagamento(PagamentoId pagamentoId, double valor, StatusPagamento status, Date dataPagamento) {
    this.pagamentoId = exigirNaoNulo(pagamentoId, "O id do pagamento não pode ser nulo");
    exigirEstado(valor > 0, "O valor deve ser positivo");
    this.valor = valor;
        this.status = exigirNaoNulo(status, "O status não pode ser nulo");
        this.dataPagamento = dataPagamento;
    }

    public PagamentoId getPagamentoId() {
        return pagamentoId;
    }

    public double getValor() {
        return valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void autorizar() {
        exigirEstado(status == StatusPagamento.PENDENTE, "Apenas pagamentos pendentes podem ser autorizados");
        this.status = StatusPagamento.SUCESSO;
    }

    public void recusar() {
        exigirEstado(status == StatusPagamento.PENDENTE, "Apenas pagamentos pendentes podem ser recusados");
        this.status = StatusPagamento.FALHA;
    }

    public void cancelar() {
        exigirEstado(status == StatusPagamento.PENDENTE, "Apenas pagamentos pendentes podem ser cancelados");
        this.status = StatusPagamento.CANCELADO;
    }

    @Override
    public Pagamento clone() {
        try {
            return (Pagamento) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
