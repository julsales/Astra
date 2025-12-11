package com.astra.cinema.aplicacao.servicos;

/**
 * Interface para serviço de criação de vendas de produtos
 * Permite desacoplamento da camada de aplicação da infraestrutura
 */
public interface VendaProdutoService {

    /**
     * Cria uma venda de produto associada a uma compra
     *
     * @param produtoId ID do produto
     * @param quantidade Quantidade vendida
     * @param compraId ID da compra associada
     */
    void criarVendaParaCompra(int produtoId, int quantidade, int compraId);
}
