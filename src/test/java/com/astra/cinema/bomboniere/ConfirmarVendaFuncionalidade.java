package com.astra.cinema.bomboniere;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.bomboniere.*;
import com.astra.cinema.dominio.pagamento.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class ConfirmarVendaFuncionalidade extends CinemaFuncionalidade {
    private VendaId vendaId = new VendaId(1);
    private PagamentoId pagamentoId = new PagamentoId(1);
    
    private RuntimeException excecao;

    @Dado("que há uma venda pendente com pagamento em status {string}")
    public void que_ha_uma_venda_pendente_com_pagamento_em_status(String statusPagamento) {
        // Cria produto
        var produtoId = new ProdutoId(1);
        var produto = new Produto(produtoId, "Pipoca Grande", 15.0, 50);
        produtoService.salvar(produto);
        
        // Cria venda pendente
        var venda = new Venda(vendaId, Arrays.asList(produto), null, StatusVenda.PENDENTE);
        repositorio.salvar(venda);
        
        // Cria pagamento
        var status = StatusPagamento.valueOf(statusPagamento);
        var pagamento = new Pagamento(pagamentoId, 15.0, status, new Date());
        pagamentoService.salvar(pagamento);
    }

    @Quando("o atendente confirma a venda")
    public void o_atendente_confirma_a_venda() {
        try {
            vendaService.confirmarVenda(vendaId, pagamentoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o status da venda muda para {string}")
    public void o_status_da_venda_muda_para(String status) {
        var venda = vendaService.obter(vendaId);
        assertEquals(StatusVenda.valueOf(status), venda.getStatus());
    }

    @Dado("que a venda possui pagamento com status {string}")
    public void que_a_venda_possui_pagamento_com_status(String statusPagamento) {
        // Cria produto
        var produtoId = new ProdutoId(1);
        var produto = new Produto(produtoId, "Refrigerante", 8.0, 30);
        produtoService.salvar(produto);
        
        // Cria venda pendente
        var venda = new Venda(vendaId, Arrays.asList(produto), null, StatusVenda.PENDENTE);
        repositorio.salvar(venda);
        
        // Cria pagamento
        var status = StatusPagamento.valueOf(statusPagamento);
        var pagamento = new Pagamento(pagamentoId, 8.0, status, new Date());
        pagamentoService.salvar(pagamento);
    }

    @Quando("o atendente tenta confirmar a venda")
    public void o_atendente_tenta_confirmar_a_venda() {
        try {
            vendaService.confirmarVenda(vendaId, pagamentoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema impede a confirmação da venda")
    public void o_sistema_impede_a_confirmacao_da_venda() {
        assertNotNull(excecao);
    }

    @Então("exibe mensagem de erro")
    public void exibe_mensagem_de_erro() {
        assertTrue(excecao.getMessage().contains("não foi aprovado") || 
                  excecao.getMessage().contains("erro"));
    }
}
