package com.astra.cinema.aplicacao.bomboniere;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Caso de uso: Modificar dados de um produto
 * Responsabilidade: Orquestrar a atualização de produto
 * 
 * Padrão: Command (encapsula a operação de modificação)
 */
public class ModificarProdutoUseCase {
    private final ProdutoRepositorio produtoRepositorio;

    public ModificarProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de produtos não pode ser nulo");
        }
        this.produtoRepositorio = produtoRepositorio;
    }

    /**
     * Modifica os dados de um produto
     * 
     * @param produtoId ID do produto
     * @param novoNome Novo nome (null mantém o atual)
     * @param novoPreco Novo preço (negativo mantém o atual)
     * @param novoEstoque Novo estoque (negativo mantém o atual)
     * @return Produto atualizado
     */
    public Produto executar(ProdutoId produtoId, String novoNome, double novoPreco, int novoEstoque) {
        if (produtoId == null) {
            throw new IllegalArgumentException("O ID do produto não pode ser nulo");
        }

        // Busca o produto
        Produto produto = produtoRepositorio.obterPorId(produtoId);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado");
        }

        // Valida novos dados
        if (novoNome != null && !novoNome.isBlank()) {
            validarNome(novoNome);
        }
        if (novoPreco >= 0) {
            validarPreco(novoPreco);
        }
        if (novoEstoque >= 0) {
            validarEstoque(novoEstoque);
        }

        // Cria produto atualizado (imutabilidade)
        Produto produtoAtualizado = new Produto(
            produto.getProdutoId(),
            novoNome != null && !novoNome.isBlank() ? novoNome : produto.getNome(),
            novoPreco >= 0 ? novoPreco : produto.getPreco(),
            novoEstoque >= 0 ? novoEstoque : produto.getEstoque()
        );

        // Persiste
        produtoRepositorio.salvar(produtoAtualizado);

        return produtoAtualizado;
    }

    private void validarNome(String nome) {
        if (nome.length() < 2) {
            throw new IllegalArgumentException("O nome deve ter pelo menos 2 caracteres");
        }
    }

    private void validarPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }
        if (preco > 10000) {
            throw new IllegalArgumentException("O preço é muito alto");
        }
    }

    private void validarEstoque(int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo");
        }
    }
}
