package com.astra.cinema.dominio.bomboniere;

import com.astra.cinema.dominio.comum.ProdutoId;

import java.util.List;

public interface ProdutoRepositorio {
    void salvar(Produto produto);
    Produto obterPorId(ProdutoId produtoId);
    List<Produto> listarProdutos();
    void remover(ProdutoId produtoId);
}
