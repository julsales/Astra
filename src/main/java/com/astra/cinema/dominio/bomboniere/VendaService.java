package com.astra.cinema.dominio.bomboniere;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;
import com.astra.cinema.dominio.pagamento.StatusPagamento;

public class VendaService {
    private final VendaRepositorio vendaRepositorio;
    private final ProdutoRepositorio produtoRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio;

    public VendaService(VendaRepositorio vendaRepositorio, 
                       ProdutoRepositorio produtoRepositorio,
                       PagamentoRepositorio pagamentoRepositorio) {
        if (vendaRepositorio == null) {
            throw new IllegalArgumentException("O repositório de vendas não pode ser nulo");
        }
        if (produtoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de produtos não pode ser nulo");
        }
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        
        this.vendaRepositorio = vendaRepositorio;
        this.produtoRepositorio = produtoRepositorio;
        this.pagamentoRepositorio = pagamentoRepositorio;
    }

    public void confirmarVenda(VendaId vendaId, PagamentoId pagamentoId) {
        if (vendaId == null) {
            throw new IllegalArgumentException("O id da venda não pode ser nulo");
        }
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }
        
        var venda = vendaRepositorio.obterPorId(vendaId);
        var pagamento = pagamentoRepositorio.obterPorId(pagamentoId);
        
        if (pagamento.getStatus() != StatusPagamento.SUCESSO) {
            throw new IllegalStateException("O pagamento não foi aprovado");
        }
        
        venda.setPagamentoId(pagamentoId);
        venda.confirmar();
        vendaRepositorio.salvar(venda);
    }

    public void venderProduto(ProdutoId produtoId, int quantidade) {
        if (produtoId == null) {
            throw new IllegalArgumentException("O id do produto não pode ser nulo");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva");
        }
        
        var produto = produtoRepositorio.obterPorId(produtoId);
        produto.reduzirEstoque(quantidade);
        produtoRepositorio.salvar(produto);
    }

    public void salvar(Venda venda) {
        vendaRepositorio.salvar(venda);
    }

    public Venda obter(VendaId vendaId) {
        return vendaRepositorio.obterPorId(vendaId);
    }
}
