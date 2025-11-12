package com.astra.cinema.aplicacao.bomboniere;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Caso de uso: Adicionar um novo produto à bomboniere
 * Responsabilidade: Orquestrar a criação de um novo produto
 * 
 * Padrão: Command (encapsula a operação de adicionar produto)
 */
public class AdicionarProdutoUseCase {
    private final ProdutoRepositorio produtoRepositorio;

    public AdicionarProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de produtos não pode ser nulo");
        }
        this.produtoRepositorio = produtoRepositorio;
    }

    /**
     * Adiciona um novo produto à bomboniere
     * 
     * @param nome Nome do produto
     * @param preco Preço do produto
     * @param estoqueInicial Estoque inicial
     * @return Produto criado
     */
    public Produto executar(String nome, double preco, int estoqueInicial) {
        // Validações
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }
        if (estoqueInicial < 0) {
            throw new IllegalArgumentException("O estoque inicial não pode ser negativo");
        }

        // Gera novo ID
        ProdutoId novoId = gerarNovoId();

        // Cria o produto
        Produto novoProduto = new Produto(novoId, nome, preco, estoqueInicial);

        // Persiste
        produtoRepositorio.salvar(novoProduto);

        return novoProduto;
    }

    private ProdutoId gerarNovoId() {
        // Gera ID baseado no timestamp
        int novoId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        return new ProdutoId(novoId);
    }
}
