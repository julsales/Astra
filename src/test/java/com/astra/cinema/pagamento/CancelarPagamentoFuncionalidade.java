package com.astra.cinema.pagamento;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.pagamento.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class CancelarPagamentoFuncionalidade extends CinemaFuncionalidade {
    private PagamentoId pagamentoId = new PagamentoId(1);
    
    private RuntimeException excecao;

    @Dado("que o pagamento está com status {string}")
    public void que_o_pagamento_esta_com_status(String status) {
        var statusPagamento = StatusPagamento.valueOf(status);
        var pagamento = new Pagamento(pagamentoId, 50.0, statusPagamento, new Date());
        pagamentoService.salvar(pagamento);
    }

    @Quando("o cliente solicita cancelamento")
    public void o_cliente_solicita_cancelamento() {
        try {
            pagamentoService.cancelarPagamento(pagamentoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o pagamento é atualizado para {string}")
    public void o_pagamento_e_atualizado_para(String status) {
        var pagamento = pagamentoService.obter(pagamentoId);
        assertEquals(StatusPagamento.valueOf(status), pagamento.getStatus());
    }

    @Dado("que o pagamento já foi aprovado")
    public void que_o_pagamento_ja_foi_aprovado() {
        var pagamento = new Pagamento(pagamentoId, 50.0, StatusPagamento.SUCESSO, new Date());
        pagamentoService.salvar(pagamento);
    }

    @Quando("o cliente tenta cancelar")
    public void o_cliente_tenta_cancelar() {
        try {
            pagamentoService.cancelarPagamento(pagamentoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema rejeita a solicitação")
    public void o_sistema_rejeita_a_solicitacao() {
        assertNotNull(excecao);
    }
}
