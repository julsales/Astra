package com.astra.cinema.aplicacao.bomboniere;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.bomboniere.*;

/**
 * Caso de uso: Registrar venda de produto da bomboniere
 * Responsabilidade: Orquestrar a venda validando estoque
 */
public class VenderProdutoUseCase {
    private final ProdutoRepositorio produtoRepositorio;

    public VenderProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de produtos não pode ser nulo");
        }
        
        this.produtoRepositorio = produtoRepositorio;
    }

    public void executar(ProdutoId produtoId, int quantidade) {
        if (produtoId == null) {
            throw new IllegalArgumentException("O id do produto não pode ser nulo");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
        
        var produto = produtoRepositorio.obterPorId(produtoId);
        produto.reduzirEstoque(quantidade);
        produtoRepositorio.salvar(produto);
    }
}
