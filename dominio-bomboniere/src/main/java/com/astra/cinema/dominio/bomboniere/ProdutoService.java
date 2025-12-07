package com.astra.cinema.dominio.bomboniere;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;

import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Service de Produto - Fachada para manter compatibilidade com testes
 */
public class ProdutoService {
    private final ProdutoRepositorio produtoRepositorio;

    public ProdutoService(ProdutoRepositorio produtoRepositorio) {
        this.produtoRepositorio = exigirNaoNulo(produtoRepositorio,
            "O repositório de produtos não pode ser nulo");
    }

    public void venderProduto(ProdutoId produtoId, int quantidade) {
        exigirNaoNulo(produtoId, "O id do produto não pode ser nulo");
        exigirPositivo(quantidade, "A quantidade deve ser positiva");
        var produto = produtoRepositorio.obterPorId(produtoId);
        produto.reduzirEstoque(quantidade);
        produtoRepositorio.salvar(produto);
    }

    public void salvar(Produto produto) {
        produtoRepositorio.salvar(exigirNaoNulo(produto, "O produto não pode ser nulo"));
    }

    public Produto obter(ProdutoId produtoId) {
        exigirNaoNulo(produtoId, "O id do produto não pode ser nulo");
        return produtoRepositorio.obterPorId(produtoId);
    }
}
