package com.astra.cinema.pagamento;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.pagamento.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class AutorizarPagamentoFuncionalidade extends CinemaFuncionalidade {
    private PagamentoId pagamentoId = new PagamentoId(1);

    @Dado("que o cliente insere dados válidos do cartão")
    public void que_o_cliente_insere_dados_validos_do_cartao() {
        var pagamento = new Pagamento(pagamentoId, 100.0, StatusPagamento.PENDENTE, new Date());
        pagamentoService.salvar(pagamento);
    }

    @Quando("o gateway responde com sucesso")
    public void o_gateway_responde_com_sucesso() {
        pagamentoService.autorizarPagamento(pagamentoId);
    }

    @Então("o pagamento muda de status para {string}")
    public void o_pagamento_muda_de_status_para(String status) {
        var pagamento = pagamentoService.obter(pagamentoId);
        assertEquals(StatusPagamento.valueOf(status), pagamento.getStatus());
    }

    @Dado("que o cliente insere dados de cartão inválidos")
    public void que_o_cliente_insere_dados_de_cartao_invalidos() {
        var pagamento = new Pagamento(pagamentoId, 100.0, StatusPagamento.PENDENTE, new Date());
        pagamentoService.salvar(pagamento);
    }

    @Quando("o gateway retorna falha")
    public void o_gateway_retorna_falha() {
        pagamentoService.recusarPagamento(pagamentoId);
    }
}
