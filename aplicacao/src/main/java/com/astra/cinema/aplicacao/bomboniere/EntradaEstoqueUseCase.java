package com.astra.cinema.aplicacao.bomboniere;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Caso de uso: Registrar uma entrada de estoque (novos pedidos/fornecimentos)
 */
public class EntradaEstoqueUseCase {
    private final ProdutoRepositorio produtoRepositorio;

    public EntradaEstoqueUseCase(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) throw new IllegalArgumentException("Repositório não pode ser nulo");
        this.produtoRepositorio = produtoRepositorio;
    }

    public Produto executar(ProdutoId produtoId, int quantidade) {
        if (produtoId == null) throw new IllegalArgumentException("produtoId não pode ser nulo");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");

        Produto produto = produtoRepositorio.obterPorId(produtoId);
        if (produto == null) throw new IllegalArgumentException("Produto não encontrado");

        produto.adicionarEstoque(quantidade);
        produtoRepositorio.salvar(produto);
        return produto;
    }
}
