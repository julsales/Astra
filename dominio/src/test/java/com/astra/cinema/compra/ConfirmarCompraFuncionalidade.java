package com.astra.cinema.compra;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.pagamento.*;
import com.astra.cinema.dominio.usuario.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class ConfirmarCompraFuncionalidade extends CinemaFuncionalidade {
    private ClienteId clienteId = new ClienteId(1);
    private CompraId compraId = new CompraId(1);
    private PagamentoId pagamentoId = new PagamentoId(1);
    private SessaoId sessaoId = new SessaoId(1);
    
    private RuntimeException excecao;

    @Dado("que existe uma compra pendente associada a um pagamento com status {string}")
    public void que_existe_uma_compra_pendente_associada_a_um_pagamento_com_status(String statusPagamento) {
        // Cria cliente
        var cliente = new Cliente(clienteId, "Maria Santos", "maria@email.com");
        clienteService.salvar(cliente);
        
        // Cria filme e sessão
        var filmeId = new FilmeId(1);
        var filme = new Filme(filmeId, "Avatar 3", "Sinopse", "12", 180, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(new AssentoId("B1"), false);
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        sessaoService.salvar(sessao);
        
        // Cria ingresso
        var ingresso = new Ingresso(new IngressoId(1), sessaoId, new AssentoId("B1"),
                                   TipoIngresso.INTEIRA, StatusIngresso.VALIDO, "QR1");
        
        // Cria compra
        var compra = new Compra(compraId, clienteId, Arrays.asList(ingresso), null, StatusCompra.PENDENTE);
        repositorio.salvar(compra);
        
        // Cria pagamento
        var status = StatusPagamento.valueOf(statusPagamento);
        var pagamento = new Pagamento(pagamentoId, 50.0, status, new Date());
        pagamentoService.salvar(pagamento);
    }

    @Quando("o cliente confirma a compra")
    public void o_cliente_confirma_a_compra() {
        try {
            compraService.confirmarCompra(compraId, pagamentoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o status da compra é atualizado para {string}")
    public void o_status_da_compra_e_atualizado_para(String status) {
        var compra = repositorio.obterPorId(compraId);
        assertEquals(StatusCompra.valueOf(status), compra.getStatus());
    }

    @Então("os ingressos passam a ter status {string}")
    public void os_ingressos_passam_a_ter_status(String status) {
        var compra = repositorio.obterPorId(compraId);
        for (Ingresso ingresso : compra.getIngressos()) {
            assertEquals(StatusIngresso.valueOf(status), ingresso.getStatus());
        }
    }

    @Quando("o cliente tenta confirmar a compra")
    public void o_cliente_tenta_confirmar_a_compra() {
        try {
            compraService.confirmarCompra(compraId, pagamentoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema impede a confirmação da compra")
    public void o_sistema_impede_a_confirmacao_da_compra() {
        assertNotNull(excecao);
    }

    @Então("informa que o pagamento não foi autorizado")
    public void informa_que_o_pagamento_nao_foi_autorizado() {
        assertTrue(excecao.getMessage().contains("não foi autorizado"));
    }
}
