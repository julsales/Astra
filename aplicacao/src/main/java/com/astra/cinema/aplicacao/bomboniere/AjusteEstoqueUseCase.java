package com.astra.cinema.aplicacao.bomboniere;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Caso de uso: Ajustar o estoque (resolver divergências/perdas)
 */
public class AjusteEstoqueUseCase {
    private final ProdutoRepositorio produtoRepositorio;

    public AjusteEstoqueUseCase(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) throw new IllegalArgumentException("Repositório não pode ser nulo");
        this.produtoRepositorio = produtoRepositorio;
    }

    /**
     * Define o estoque para um valor específico (ajuste manual)
     */
    public Produto definirEstoque(ProdutoId produtoId, int novoEstoque) {
        if (produtoId == null) throw new IllegalArgumentException("produtoId não pode ser nulo");
        if (novoEstoque < 0) throw new IllegalArgumentException("estoque não pode ser negativo");

        Produto produto = produtoRepositorio.obterPorId(produtoId);
        if (produto == null) throw new IllegalArgumentException("Produto não encontrado");

        // Cria nova instância do domínio com estoque ajustado
        Produto produtoAjustado = new Produto(
            produto.getProdutoId(),
            produto.getNome(),
            produto.getPreco(),
            novoEstoque
        );

        produtoRepositorio.salvar(produtoAjustado);
        return produtoAjustado;
    }
}
