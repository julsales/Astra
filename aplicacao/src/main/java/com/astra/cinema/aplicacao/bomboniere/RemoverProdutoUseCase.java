package com.astra.cinema.aplicacao.bomboniere;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Caso de uso: Remover um produto da bomboniere
 * Responsabilidade: Orquestrar a remoção de produto
 * 
 * Padrão: Command (encapsula a operação de remoção)
 */
public class RemoverProdutoUseCase {
    private final ProdutoRepositorio produtoRepositorio;

    public RemoverProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de produtos não pode ser nulo");
        }
        this.produtoRepositorio = produtoRepositorio;
    }

    /**
     * Remove um produto da bomboniere
     * 
     * @param produtoId ID do produto
     * @throws IllegalArgumentException se o produto não existir
     */
    public void executar(ProdutoId produtoId) {
        if (produtoId == null) {
            throw new IllegalArgumentException("O ID do produto não pode ser nulo");
        }

        // Busca o produto
        Produto produto = produtoRepositorio.obterPorId(produtoId);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado");
        }

        // Delegar remoção ao repositório
        produtoRepositorio.remover(produtoId);
    }

    /**
     * Verifica se um produto pode ser removido
     * 
     * @param produtoId ID do produto
     * @return true se pode ser removido
     */
    public boolean podeRemover(ProdutoId produtoId) {
        if (produtoId == null) {
            return false;
        }

        Produto produto = produtoRepositorio.obterPorId(produtoId);
        return produto != null;
    }
}
