package com.astra.compra.dominio.ingresso;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.compra.dominio.CompraFuncionalidade;
import com.astra.compra.dominio.sessao.AssentoId;
import com.astra.compra.dominio.sessao.SessaoId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CancelarCompraFuncionalidade extends CompraFuncionalidade {
    private RuntimeException excecao;

    @Given("uma compra pendente com {int} ingressos")
    public void uma_compra_pendente_com_ingressos(Integer quantidade) {
        Compra compra = compraServico.iniciarCompra(StepsCompartilhados.getClienteId());
        StepsCompartilhados.setCompraId(compra.getId());
        
        for (int i = 1; i <= quantidade; i++) {
            compraServico.adicionarIngresso(
                StepsCompartilhados.getCompraId(),
                new SessaoId(1),
                new AssentoId("A" + i),
                TipoIngresso.INTEIRA
            );
        }
    }

    @Given("uma compra confirmada com {int} ingressos válidos")
    public void uma_compra_confirmada_com_ingressos_validos(Integer quantidade) {
        uma_compra_pendente_com_ingressos(quantidade);
        compraServico.confirmarCompra(StepsCompartilhados.getCompraId());
    }

    @Given("uma compra confirmada com um ingresso já utilizado")
    public void uma_compra_confirmada_com_um_ingresso_ja_utilizado() {
        Compra compra = compraServico.iniciarCompra(StepsCompartilhados.getClienteId());
        StepsCompartilhados.setCompraId(compra.getId());
        
        compraServico.adicionarIngresso(
            StepsCompartilhados.getCompraId(),
            new SessaoId(1),
            new AssentoId("A1"),
            TipoIngresso.INTEIRA
        );
        
        compraServico.confirmarCompra(StepsCompartilhados.getCompraId());
        
        // Marca o ingresso como utilizado
        Compra compraAtual = compraRepositorio.obter(StepsCompartilhados.getCompraId());
        compraAtual.getIngressos().get(0).utilizar();
        compraRepositorio.salvar(compraAtual);
    }

    @Given("uma compra já cancelada")
    public void uma_compra_ja_cancelada() {
        uma_compra_pendente_com_ingressos(1);
        compraServico.cancelarCompra(StepsCompartilhados.getCompraId());
    }

    @When("o cliente cancela a compra")
    public void o_cliente_cancela_a_compra() {
        compraServico.cancelarCompra(StepsCompartilhados.getCompraId());
    }

    @When("o cliente tenta cancelar a compra")
    public void o_cliente_tenta_cancelar_a_compra() {
        try {
            compraServico.cancelarCompra(StepsCompartilhados.getCompraId());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("o cliente tenta cancelar a compra novamente")
    public void o_cliente_tenta_cancelar_a_compra_novamente() {
        o_cliente_tenta_cancelar_a_compra();
    }

    @Then("a compra tem status {string}")
    public void a_compra_tem_status(String statusEsperado) {
        Compra compra = compraServico.obterCompra(StepsCompartilhados.getCompraId());
        assertEquals(StatusCompra.valueOf(statusEsperado), compra.getStatus());
    }

    @Then("todos os ingressos estão cancelados")
    public void todos_os_ingressos_estao_cancelados() {
        Compra compra = compraServico.obterCompra(StepsCompartilhados.getCompraId());
        
        for (Ingresso ingresso : compra.getIngressos()) {
            assertEquals(StatusIngresso.CANCELADO, ingresso.getStatus());
        }
    }

    @Then("o sistema informa que não é possível cancelar")
    public void o_sistema_informa_que_nao_e_possivel_cancelar() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("utilizado") || 
                   excecao.getMessage().contains("cancelar"));
    }

    @Then("a compra continua com status {string}")
    public void a_compra_continua_com_status(String statusEsperado) {
        a_compra_tem_status(statusEsperado);
    }

    @Then("o sistema informa que a compra já está cancelada")
    public void o_sistema_informa_que_a_compra_ja_esta_cancelada() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("já está cancelada"));
    }
}
