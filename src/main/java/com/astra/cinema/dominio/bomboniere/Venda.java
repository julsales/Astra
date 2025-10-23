package com.astra.cinema.dominio.bomboniere;

import com.astra.cinema.dominio.comum.*;
import java.util.ArrayList;
import java.util.List;

public class Venda implements Cloneable {
    private final VendaId vendaId;
    private List<Produto> produtos;
    private PagamentoId pagamentoId;
    private StatusVenda status;

    public Venda(VendaId vendaId, List<Produto> produtos, PagamentoId pagamentoId, StatusVenda status) {
        if (vendaId == null) {
            throw new IllegalArgumentException("O id da venda não pode ser nulo");
        }
        if (produtos == null || produtos.isEmpty()) {
            throw new IllegalArgumentException("A venda deve ter pelo menos um produto");
        }
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }
        
        this.vendaId = vendaId;
        this.produtos = new ArrayList<>(produtos);
        this.pagamentoId = pagamentoId;
        this.status = status;
    }

    public VendaId getVendaId() {
        return vendaId;
    }

    public List<Produto> getProdutos() {
        return new ArrayList<>(produtos);
    }

    public PagamentoId getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(PagamentoId pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void confirmar() {
        if (status != StatusVenda.PENDENTE) {
            throw new IllegalStateException("Apenas vendas pendentes podem ser confirmadas");
        }
        if (pagamentoId == null) {
            throw new IllegalStateException("A venda deve ter um pagamento associado");
        }
        this.status = StatusVenda.CONFIRMADA;
    }

    public void cancelar() {
        if (status == StatusVenda.CANCELADA) {
            throw new IllegalStateException("A venda já está cancelada");
        }
        this.status = StatusVenda.CANCELADA;
    }

    @Override
    public Venda clone() {
        try {
            Venda cloned = (Venda) super.clone();
            cloned.produtos = new ArrayList<>();
            for (Produto produto : this.produtos) {
                cloned.produtos.add(produto.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
