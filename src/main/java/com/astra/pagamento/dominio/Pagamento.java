package com.astra.pagamento.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public class Pagamento implements Cloneable {
    private final PagamentoId id;
    private final double valor;
    private final MetodoPagamentoId metodoPagamentoId;
    private Transacao transacao;
    private StatusPagamento status;
    private LocalDateTime dataPagamento;

    // Construtor para criação (sem ID)
    public Pagamento(double valor, MetodoPagamentoId metodoPagamentoId) {
        this.id = null;
        
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        Objects.requireNonNull(metodoPagamentoId, "O ID do método de pagamento não pode ser nulo");
        
        this.valor = valor;
        this.metodoPagamentoId = metodoPagamentoId;
        this.status = StatusPagamento.PENDENTE;
        this.dataPagamento = LocalDateTime.now();
    }

    // Construtor para persistência (com ID)
    public Pagamento(PagamentoId id, double valor, MetodoPagamentoId metodoPagamentoId) {
        Objects.requireNonNull(id, "O ID não pode ser nulo");
        
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        Objects.requireNonNull(metodoPagamentoId, "O ID do método de pagamento não pode ser nulo");
        
        this.id = id;
        this.valor = valor;
        this.metodoPagamentoId = metodoPagamentoId;
        this.status = StatusPagamento.PENDENTE;
        this.dataPagamento = LocalDateTime.now();
    }

    public PagamentoId getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public MetodoPagamentoId getMetodoPagamentoId() {
        return metodoPagamentoId;
    }

    public Transacao getTransacao() {
        return transacao != null ? transacao.clone() : null;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void autorizar(Transacao transacao, boolean sucesso) {
        if (status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos pendentes podem ser autorizados");
        }
        
        Objects.requireNonNull(transacao, "A transação não pode ser nula");
        this.transacao = transacao;
        this.status = sucesso ? StatusPagamento.SUCESSO : StatusPagamento.FALHA;
    }

    public void confirmar() {
        if (status != StatusPagamento.SUCESSO) {
            throw new IllegalStateException("Apenas pagamentos bem-sucedidos podem ser confirmados");
        }
        // Confirmação já está implícita no status SUCESSO
    }

    public void cancelar() {
        if (status == StatusPagamento.CANCELADO) {
            throw new IllegalStateException("O pagamento já está cancelado");
        }
        
        if (status == StatusPagamento.SUCESSO) {
            throw new IllegalStateException("Não é possível cancelar um pagamento bem-sucedido");
        }
        
        this.status = StatusPagamento.CANCELADO;
    }

    @Override
    public Pagamento clone() {
        try {
            Pagamento cloned = (Pagamento) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Erro ao clonar pagamento", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(id, pagamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
