package com.astra.cinema.bomboniere;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.bomboniere.*;
import io.cucumber.java.pt.*;

public class VenderProdutoFuncionalidade extends CinemaFuncionalidade {
    private ProdutoId produtoId = new ProdutoId(1);
    
    private RuntimeException excecao;

    @Dado("que o produto {string} tem {int} unidades no estoque")
    public void que_o_produto_tem_unidades_no_estoque(String nomeProduto, Integer quantidade) {
        var produto = new Produto(produtoId, nomeProduto, 10.0, quantidade);
        produtoService.salvar(produto);
    }

    @Dado("que o produto {string} tem {int} unidade no estoque")
    public void que_o_produto_tem_unidade_no_estoque(String nomeProduto, Integer quantidade) {
        var produto = new Produto(produtoId, nomeProduto, 10.0, quantidade);
        produtoService.salvar(produto);
    }

    @Quando("o cliente compra {int} unidades")
    public void o_cliente_compra_unidades(Integer quantidade) {
        try {
            vendaService.venderProduto(produtoId, quantidade);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o estoque é reduzido para {int}")
    public void o_estoque_e_reduzido_para(Integer estoqueEsperado) {
        var produto = produtoService.obter(produtoId);
        assertEquals(estoqueEsperado, produto.getEstoque());
    }

    @Quando("o cliente tenta comprar {int} unidades")
    public void o_cliente_tenta_comprar_unidades(Integer quantidade) {
        try {
            vendaService.venderProduto(produtoId, quantidade);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema recusa a venda")
    public void o_sistema_recusa_a_venda() {
        assertNotNull(excecao);
    }

    @Então("informa que o estoque é insuficiente")
    public void informa_que_o_estoque_e_insuficiente() {
        assertTrue(excecao.getMessage().contains("insuficiente") || 
                  excecao.getMessage().contains("Estoque"));
    }
}
