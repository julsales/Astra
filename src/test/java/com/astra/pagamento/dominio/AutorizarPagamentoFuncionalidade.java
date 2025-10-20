package com.astra.pagamento.dominio;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.UUID;

public class AutorizarPagamentoFuncionalidade extends PagamentoFuncionalidade {
    private RuntimeException excecao;

    @Given("um pagamento pendente de valor {double} com método {string}")
    public void um_pagamento_pendente_de_valor_com_metodo(Double valor, String nomeMetodo) {
        MetodoPagamento metodo = encontrarMetodoPorNome(nomeMetodo);
        Pagamento pagamento = pagamentoServico.iniciarPagamento(valor, metodo.getId());
        StepsCompartilhados.setPagamentoId(pagamento.getId());
        StepsCompartilhados.setMetodoPagamentoId(metodo.getId());
    }

    @Given("um pagamento já autorizado com sucesso")
    public void um_pagamento_ja_autorizado_com_sucesso() {
        MetodoPagamento metodo = encontrarMetodoPorNome("PIX");
        Pagamento pagamento = pagamentoServico.iniciarPagamento(100.0, metodo.getId());
        StepsCompartilhados.setPagamentoId(pagamento.getId());
        
        Transacao transacao = new Transacao(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            "Gateway-Test",
            "Aprovado"
        );
        
        pagamentoServico.autorizarPagamento(pagamento.getId(), transacao, true);
    }

    @When("o sistema autoriza o pagamento com sucesso")
    public void o_sistema_autoriza_o_pagamento_com_sucesso() {
        Transacao transacao = new Transacao(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            "Gateway-Test",
            "Aprovado"
        );
        
        pagamentoServico.autorizarPagamento(StepsCompartilhados.getPagamentoId(), transacao, true);
    }

    @When("o sistema autoriza o pagamento com falha")
    public void o_sistema_autoriza_o_pagamento_com_falha() {
        Transacao transacao = new Transacao(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            "Gateway-Test",
            "Recusado - Saldo insuficiente"
        );
        
        pagamentoServico.autorizarPagamento(StepsCompartilhados.getPagamentoId(), transacao, false);
    }

    @When("o sistema tenta autorizar o pagamento novamente")
    public void o_sistema_tenta_autorizar_o_pagamento_novamente() {
        try {
            Transacao transacao = new Transacao(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "Gateway-Test",
                "Aprovado"
            );
            
            pagamentoServico.autorizarPagamento(StepsCompartilhados.getPagamentoId(), transacao, true);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o pagamento tem status {string}")
    public void o_pagamento_tem_status(String statusEsperado) {
        Pagamento pagamento = pagamentoServico.obterPagamento(StepsCompartilhados.getPagamentoId());
        assertEquals(StatusPagamento.valueOf(statusEsperado), pagamento.getStatus());
    }

    @Then("a transação foi registrada")
    public void a_transacao_foi_registrada() {
        Pagamento pagamento = pagamentoServico.obterPagamento(StepsCompartilhados.getPagamentoId());
        assertNotNull(pagamento.getTransacao());
        assertNotNull(pagamento.getTransacao().getCodigoTransacao());
    }

    @Then("o sistema informa que o pagamento não está pendente")
    public void o_sistema_informa_que_o_pagamento_nao_esta_pendente() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("pendente"));
    }

    private MetodoPagamento encontrarMetodoPorNome(String nome) {
        return metodoPagamentoRepositorio.listarAtivos().stream()
            .filter(m -> m.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Método não encontrado: " + nome));
    }
}
