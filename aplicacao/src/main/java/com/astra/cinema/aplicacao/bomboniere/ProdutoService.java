package com.astra.cinema.aplicacao.bomboniere;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Service de Produto - Fachada para manter compatibilidade com testes
 */
public class ProdutoService {
    private final ProdutoRepositorio produtoRepositorio;
    private final VenderProdutoUseCase venderProdutoUseCase;

    public ProdutoService(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de produtos não pode ser nulo");
        }

        this.produtoRepositorio = produtoRepositorio;
        this.venderProdutoUseCase = new VenderProdutoUseCase(produtoRepositorio);
    }

    public void venderProduto(ProdutoId produtoId, int quantidade) {
        venderProdutoUseCase.executar(produtoId, quantidade);
    }

    public void salvar(Produto produto) {
        produtoRepositorio.salvar(produto);
    }

    public Produto obter(ProdutoId produtoId) {
        return produtoRepositorio.obterPorId(produtoId);
    }
}
