package com.astra.bdd;

import com.astra.bdd.TestContext;
import com.astra.model.*;
import com.astra.repository.*;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BomboniereSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaBomboniereRepository vendaRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    // Regra de Negócio 7 - Confirmar venda com pagamento
    @Dado("que há uma venda pendente com pagamento em status {string}")
    public void queHaUmaVendaPendenteComPagamentoEmStatus(String statusPagamento) {
        context.inicializarFuncionarioPadrao();
        
        VendaBomboniere venda = new VendaBomboniere(context.getFuncionario(), new BigDecimal("50.00"));
        venda = vendaRepository.save(venda);
        
        Pagamento pagamento = new Pagamento(venda.getValorTotal(), Pagamento.FormaPagamento.DINHEIRO);
        pagamento.setStatus(Pagamento.StatusPagamento.valueOf(statusPagamento));
        venda.setPagamento(pagamento);
        
        pagamentoRepository.save(pagamento);
        vendaRepository.save(venda);
        
        context.setVendaBomboniere(venda);
    }

    @Quando("o atendente confirma a venda")
    public void oAtendenteConfirmaAVenda() {
        try {
            VendaBomboniere venda = context.getVendaBomboniere();
            venda.confirmar();
            vendaRepository.save(venda);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o status da venda muda para {string}")
    public void oStatusDaVendaMudaPara(String status) {
        VendaBomboniere venda = vendaRepository.findById(context.getVendaBomboniere().getId()).orElse(null);
        assertNotNull(venda);
        assertEquals(VendaBomboniere.StatusVenda.valueOf(status), venda.getStatus());
    }

    @Dado("que a venda possui pagamento com status {string}")
    public void queAVendaPossuiPagamentoComStatus(String statusPagamento) {
        queHaUmaVendaPendenteComPagamentoEmStatus(statusPagamento);
    }

    @Quando("o atendente tenta confirmar a venda")
    public void oAtendenteTentaConfirmarAVenda() {
        oAtendenteConfirmaAVenda();
    }

    @Então("exibe mensagem de erro")
    public void exibeMensagemDeErro() {
        assertNotNull(context.getMensagemErro());
        assertTrue(context.getMensagemErro().contains("não foi autorizado"));
    }

    // Regra de Negócio 8 - Controle de estoque
    @Dado("que o produto {string} tem {int} unidades no estoque")
    public void queOProdutoTemUnidadesNoEstoque(String nomeProduto, Integer estoque) {
        Produto produto = new Produto(nomeProduto, new BigDecimal("15.00"), estoque);
        produto = produtoRepository.save(produto);
        context.setProduto(produto);
    }
    
    @Dado("que o produto {string} tem {int} unidade no estoque")
    public void queOProdutoTemUnidadeNoEstoque(String nomeProduto, Integer estoque) {
        queOProdutoTemUnidadesNoEstoque(nomeProduto, estoque);
    }

    @Quando("o cliente compra {int} unidades")
    public void oClienteCompraUnidades(Integer quantidade) {
        Produto produto = context.getProduto();
        produto.reduzirEstoque(quantidade);
        produtoRepository.save(produto);
    }

    @Então("o estoque é reduzido para {int}")
    public void oEstoqueEReduzidoPara(Integer estoqueEsperado) {
        Produto produto = produtoRepository.findById(context.getProduto().getId()).orElse(null);
        assertNotNull(produto);
        assertEquals(estoqueEsperado, produto.getEstoque());
    }

    @Quando("o cliente tenta comprar {int} unidades")
    public void oClienteTentaComprarUnidades(Integer quantidade) {
        try {
            Produto produto = context.getProduto();
            produto.reduzirEstoque(quantidade);
            produtoRepository.save(produto);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema recusa a venda")
    public void oSistemaRecusaAVenda() {
        assertNotNull(context.getException());
    }

    @Então("informa que o estoque é insuficiente")
    public void informaQueOEstoqueEInsuficiente() {
        assertTrue(context.getMensagemErro().contains("insuficiente"));
    }
}
