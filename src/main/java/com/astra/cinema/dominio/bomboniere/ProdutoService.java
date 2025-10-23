package com.astra.cinema.dominio.bomboniere;

import com.astra.cinema.dominio.comum.ProdutoId;

public class ProdutoService {
    private final ProdutoRepositorio produtoRepositorio;

    public ProdutoService(ProdutoRepositorio produtoRepositorio) {
        if (produtoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de produtos não pode ser nulo");
        }
        
        this.produtoRepositorio = produtoRepositorio;
    }

    public void salvar(Produto produto) {
        produtoRepositorio.salvar(produto);
    }

    public Produto obter(ProdutoId produtoId) {
        return produtoRepositorio.obterPorId(produtoId);
    }
}
