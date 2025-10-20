package com.astra.compra.dominio.ingresso;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.compra.dominio.CompraFuncionalidade;
import com.astra.compra.dominio.sessao.AssentoId;
import com.astra.compra.dominio.sessao.SessaoId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SelecionarTipoIngressoFuncionalidade extends CompraFuncionalidade {
    private TipoIngresso tipoSelecionado;
    private RuntimeException excecao;

    @Given("uma compra pendente")
    public void uma_compra_pendente() {
        Compra compra = compraServico.iniciarCompra(StepsCompartilhados.getClienteId());
        StepsCompartilhados.setCompraId(compra.getId());
    }

    @Given("uma compra confirmada com ingresso do tipo {string}")
    public void uma_compra_confirmada_com_ingresso_do_tipo(String tipo) {
        uma_compra_pendente();
        
        compraServico.adicionarIngresso(
            StepsCompartilhados.getCompraId(),
            new SessaoId(1),
            new AssentoId("A1"),
            TipoIngresso.valueOf(tipo)
        );
        
        compraServico.confirmarCompra(StepsCompartilhados.getCompraId());
    }

    @When("o cliente adiciona um ingresso do tipo {string}")
    public void o_cliente_adiciona_um_ingresso_do_tipo(String tipo) {
        tipoSelecionado = TipoIngresso.valueOf(tipo);
        
        compraServico.adicionarIngresso(
            StepsCompartilhados.getCompraId(),
            new SessaoId(1),
            new AssentoId("A1"),
            tipoSelecionado
        );
    }

    @When("o cliente tenta adicionar outro ingresso")
    public void o_cliente_tenta_adicionar_outro_ingresso() {
        try {
            compraServico.adicionarIngresso(
                StepsCompartilhados.getCompraId(),
                new SessaoId(1),
                new AssentoId("A2"),
                TipoIngresso.INTEIRA
            );
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o ingresso é adicionado com o tipo {string}")
    public void o_ingresso_e_adicionado_com_o_tipo(String tipoEsperado) {
        Compra compra = compraServico.obterCompra(StepsCompartilhados.getCompraId());
        assertFalse(compra.getIngressos().isEmpty());
        
        Ingresso ultimoIngresso = compra.getIngressos().get(compra.getIngressos().size() - 1);
        assertEquals(TipoIngresso.valueOf(tipoEsperado), ultimoIngresso.getTipo());
    }

    @Then("o sistema não permite adicionar ingressos após confirmação")
    public void o_sistema_nao_permite_adicionar_ingressos_apos_confirmacao() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("pendente") || 
                   excecao.getMessage().contains("não está pendente"));
    }
}
