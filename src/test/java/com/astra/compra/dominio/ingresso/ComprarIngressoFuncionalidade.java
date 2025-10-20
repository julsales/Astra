package com.astra.compra.dominio.ingresso;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.compra.dominio.CompraFuncionalidade;
import com.astra.compra.dominio.sessao.AssentoId;
import com.astra.compra.dominio.sessao.SessaoId;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ComprarIngressoFuncionalidade extends CompraFuncionalidade {

    @When("o cliente inicia uma compra")
    public void o_cliente_inicia_uma_compra() {
        Compra compra = compraServico.iniciarCompra(StepsCompartilhados.getClienteId());
        StepsCompartilhados.setCompraId(compra.getId());
    }

    @When("adiciona um ingresso {string} para a sessão {int} no assento {string}")
    public void adiciona_um_ingresso_para_a_sessao_no_assento(String tipoStr, Integer sessaoIdValor, String assentoIdValor) {
        SessaoId sessaoId = new SessaoId(sessaoIdValor);
        AssentoId assentoId = new AssentoId(assentoIdValor);
        TipoIngresso tipo = mapearTipo(tipoStr);
        
        compraServico.adicionarIngresso(StepsCompartilhados.getCompraId(), sessaoId, assentoId, tipo);
    }

    @Then("a compra é criada com status {string}")
    public void a_compra_e_criada_com_status(String statusEsperado) {
        Compra compra = compraServico.obterCompra(StepsCompartilhados.getCompraId());
        assertEquals(StatusCompra.valueOf(statusEsperado), compra.getStatus());
    }

    @Then("a compra possui {int} ingresso(s)")
    public void a_compra_possui_ingressos(Integer quantidade) {
        Compra compra = compraServico.obterCompra(StepsCompartilhados.getCompraId());
        assertEquals(quantidade, compra.getIngressos().size());
    }

    @Then("o ingresso tem tipo {string}")
    public void o_ingresso_tem_tipo(String tipoEsperado) {
        Compra compra = compraServico.obterCompra(StepsCompartilhados.getCompraId());
        assertFalse(compra.getIngressos().isEmpty());
        
        Ingresso ultimoIngresso = compra.getIngressos().get(compra.getIngressos().size() - 1);
        assertEquals(TipoIngresso.valueOf(tipoEsperado), ultimoIngresso.getTipo());
    }

    private TipoIngresso mapearTipo(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "inteiro", "inteira" -> TipoIngresso.INTEIRA;
            case "meia", "meia-entrada" -> TipoIngresso.MEIA;
            case "promocional", "promocao" -> TipoIngresso.PROMOCAO;
            default -> throw new IllegalArgumentException("Tipo de ingresso inválido: " + tipo);
        };
    }
}
