package com.astra.pagamento.dominio;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

public class SelecionarMetodoPagamentoFuncionalidade extends PagamentoFuncionalidade {
    private List<MetodoPagamento> metodosDisponiveis;
    private RuntimeException excecao;

    @Given("um método de pagamento inativo {string}")
    public void um_metodo_de_pagamento_inativo(String nomeMetodo) {
        MetodoPagamento metodo = encontrarMetodoPorNome(nomeMetodo);
        metodo.desativar();
        metodoPagamentoRepositorio.salvar(metodo);
        StepsCompartilhados.setMetodoPagamentoId(metodo.getId());
    }

    @When("o cliente consulta os métodos de pagamento disponíveis")
    public void o_cliente_consulta_os_metodos_de_pagamento_disponiveis() {
        metodosDisponiveis = pagamentoServico.listarMetodosDisponiveis();
    }

    @When("o cliente inicia um pagamento de valor {double} com método {string}")
    public void o_cliente_inicia_um_pagamento_de_valor_com_metodo(Double valor, String nomeMetodo) {
        MetodoPagamento metodo = encontrarMetodoPorNome(nomeMetodo);
        Pagamento pagamento = pagamentoServico.iniciarPagamento(valor, metodo.getId());
        StepsCompartilhados.setPagamentoId(pagamento.getId());
    }

    @When("o cliente tenta iniciar um pagamento com o método inativo")
    public void o_cliente_tenta_iniciar_um_pagamento_com_o_metodo_inativo() {
        try {
            pagamentoServico.iniciarPagamento(100.0, StepsCompartilhados.getMetodoPagamentoId());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema exibe {int} métodos de pagamento")
    public void o_sistema_exibe_metodos_de_pagamento(Integer quantidade) {
        assertEquals(quantidade, metodosDisponiveis.size());
    }

    @Then("os métodos incluem {string}, {string}, {string} e {string}")
    public void os_metodos_incluem(String metodo1, String metodo2, String metodo3, String metodo4) {
        List<String> nomesMetodos = metodosDisponiveis.stream()
            .map(MetodoPagamento::getNome)
            .toList();
        
        assertTrue(nomesMetodos.contains(metodo1));
        assertTrue(nomesMetodos.contains(metodo2));
        assertTrue(nomesMetodos.contains(metodo3));
        assertTrue(nomesMetodos.contains(metodo4));
    }

    @Then("o pagamento é criado com status {string}")
    public void o_pagamento_e_criado_com_status(String statusEsperado) {
        Pagamento pagamento = pagamentoServico.obterPagamento(StepsCompartilhados.getPagamentoId());
        assertEquals(StatusPagamento.valueOf(statusEsperado), pagamento.getStatus());
    }

    @Then("o valor do pagamento é {double}")
    public void o_valor_do_pagamento_e(Double valorEsperado) {
        Pagamento pagamento = pagamentoServico.obterPagamento(StepsCompartilhados.getPagamentoId());
        assertEquals(valorEsperado, pagamento.getValor(), 0.01);
    }

    @Then("o sistema informa que o método não está ativo")
    public void o_sistema_informa_que_o_metodo_nao_esta_ativo() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("não está ativo"));
    }

    private MetodoPagamento encontrarMetodoPorNome(String nome) {
        return metodoPagamentoRepositorio.listarTodos().stream()
            .filter(m -> m.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Método não encontrado: " + nome));
    }
}
