package com.astra.cinema.dominio.pagamento;

import com.astra.cinema.dominio.comum.PagamentoId;
import java.util.Date;

public class Pagamento implements Cloneable {
    private final PagamentoId pagamentoId;
    private double valor;
    private StatusPagamento status;
    private Date dataPagamento;

    public Pagamento(PagamentoId pagamentoId, double valor, StatusPagamento status, Date dataPagamento) {
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo");
        }
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }
        
        this.pagamentoId = pagamentoId;
        this.valor = valor;
        this.status = status;
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
        if (status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos pendentes podem ser autorizados");
        }
        this.status = StatusPagamento.SUCESSO;
    }

    public void recusar() {
        if (status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos pendentes podem ser recusados");
        }
        this.status = StatusPagamento.FALHA;
    }

    public void cancelar() {
        if (status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos pendentes podem ser cancelados");
        }
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
