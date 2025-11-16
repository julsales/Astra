package com.astra.cinema.dominio.bomboniere;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;

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
        this.vendaRepositorio = exigirNaoNulo(vendaRepositorio, "O repositório de vendas não pode ser nulo");
        this.produtoRepositorio = exigirNaoNulo(produtoRepositorio, "O repositório de produtos não pode ser nulo");
        this.pagamentoRepositorio = exigirNaoNulo(pagamentoRepositorio, "O repositório de pagamentos não pode ser nulo");
    }

    public void confirmarVenda(VendaId vendaId, PagamentoId pagamentoId) {
        exigirNaoNulo(vendaId, "O id da venda não pode ser nulo");
        exigirNaoNulo(pagamentoId, "O id do pagamento não pode ser nulo");
        
        var venda = exigirNaoNulo(vendaRepositorio.obterPorId(vendaId), "Venda não encontrada");
        var pagamento = exigirNaoNulo(pagamentoRepositorio.obterPorId(pagamentoId), "Pagamento não encontrado");
        
        exigirEstado(pagamento.getStatus() == StatusPagamento.SUCESSO, "O pagamento não foi aprovado");
        
        venda.setPagamentoId(pagamentoId);
        venda.confirmar();
        vendaRepositorio.salvar(venda);
    }

    public void venderProduto(ProdutoId produtoId, int quantidade) {
        exigirNaoNulo(produtoId, "O id do produto não pode ser nulo");
        exigirPositivo(quantidade, "A quantidade deve ser positiva");
        
        var produto = exigirNaoNulo(produtoRepositorio.obterPorId(produtoId), "Produto não encontrado");
        produto.reduzirEstoque(quantidade);
        produtoRepositorio.salvar(produto);
    }

    public void salvar(Venda venda) {
        vendaRepositorio.salvar(exigirNaoNulo(venda, "A venda não pode ser nula"));
    }

    public Venda obter(VendaId vendaId) {
        exigirNaoNulo(vendaId, "O id da venda não pode ser nulo");
        return vendaRepositorio.obterPorId(vendaId);
    }
}
