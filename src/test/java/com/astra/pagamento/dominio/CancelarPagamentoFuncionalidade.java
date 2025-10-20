package com.astra.pagamento.dominio;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.UUID;

public class CancelarPagamentoFuncionalidade extends PagamentoFuncionalidade {
    private RuntimeException excecao;

    @Given("um pagamento com falha na autorização")
    public void um_pagamento_com_falha_na_autorizacao() {
        MetodoPagamento metodo = encontrarMetodoPorNome("Cartão de Crédito");
        Pagamento pagamento = pagamentoServico.iniciarPagamento(100.0, metodo.getId());
        StepsCompartilhados.setPagamentoId(pagamento.getId());
        
        Transacao transacao = new Transacao(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            "Gateway-Test",
            "Recusado"
        );
        
        pagamentoServico.autorizarPagamento(pagamento.getId(), transacao, false);
    }

    @Given("um pagamento já cancelado")
    public void um_pagamento_ja_cancelado() {
        MetodoPagamento metodo = encontrarMetodoPorNome("PIX");
        Pagamento pagamento = pagamentoServico.iniciarPagamento(50.0, metodo.getId());
        StepsCompartilhados.setPagamentoId(pagamento.getId());
        
        pagamentoServico.cancelarPagamento(pagamento.getId());
    }

    @When("o cliente cancela o pagamento")
    public void o_cliente_cancela_o_pagamento() {
        pagamentoServico.cancelarPagamento(StepsCompartilhados.getPagamentoId());
    }

    @When("o cliente tenta cancelar o pagamento")
    public void o_cliente_tenta_cancelar_o_pagamento() {
        try {
            pagamentoServico.cancelarPagamento(StepsCompartilhados.getPagamentoId());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("o cliente tenta cancelar o pagamento novamente")
    public void o_cliente_tenta_cancelar_o_pagamento_novamente() {
        o_cliente_tenta_cancelar_o_pagamento();
    }

    @Then("o sistema informa que não é possível cancelar pagamento bem-sucedido")
    public void o_sistema_informa_que_nao_e_possivel_cancelar_pagamento_bem_sucedido() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("bem-sucedido") || 
                   excecao.getMessage().contains("sucesso"));
    }

    @Then("o sistema informa que o pagamento já está cancelado")
    public void o_sistema_informa_que_o_pagamento_ja_esta_cancelado() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("já está cancelado"));
    }

    private MetodoPagamento encontrarMetodoPorNome(String nome) {
        return metodoPagamentoRepositorio.listarAtivos().stream()
            .filter(m -> m.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Método não encontrado: " + nome));
    }
}
