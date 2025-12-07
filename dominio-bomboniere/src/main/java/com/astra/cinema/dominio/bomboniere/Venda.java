package com.astra.cinema.dominio.bomboniere;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirColecaoNaoVazia;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.pagamento.StatusPagamento;
import java.util.ArrayList;
import java.util.List;

public class Venda implements Cloneable {
    private final VendaId vendaId;
    private List<Produto> produtos;
    private PagamentoId pagamentoId;
    private StatusVenda status;

    public Venda(VendaId vendaId, List<Produto> produtos, PagamentoId pagamentoId, StatusVenda status) {
        this.vendaId = exigirNaoNulo(vendaId, "O id da venda não pode ser nulo");
        this.produtos = new ArrayList<>(exigirColecaoNaoVazia(produtos, "A venda deve ter pelo menos um produto"));
        this.pagamentoId = pagamentoId;
        this.status = exigirNaoNulo(status, "O status não pode ser nulo");
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

    /**
     * Confirma a venda verificando se o pagamento foi aprovado.
     * RN7: Uma venda na bomboniere só é confirmada após pagamento aprovado.
     *
     * @param statusPagamento O status atual do pagamento associado
     */
    public void confirmar(StatusPagamento statusPagamento) {
        exigirEstado(status == StatusVenda.PENDENTE, "Apenas vendas pendentes podem ser confirmadas");
        exigirEstado(pagamentoId != null, "A venda deve ter um pagamento associado");
        exigirEstado(statusPagamento == StatusPagamento.SUCESSO,
            "O pagamento não foi aprovado");
        this.status = StatusVenda.CONFIRMADA;
    }

    public void cancelar() {
        exigirEstado(status != StatusVenda.CANCELADA, "A venda já está cancelada");
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
