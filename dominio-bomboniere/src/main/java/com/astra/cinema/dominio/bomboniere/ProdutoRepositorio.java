package com.astra.cinema.dominio.bomboniere;

import java.util.List;

import com.astra.cinema.dominio.comum.ProdutoId;

public interface ProdutoRepositorio {
    void salvar(Produto produto);
    Produto obterPorId(ProdutoId produtoId);
    List<Produto> listarProdutos();
    void remover(ProdutoId produtoId);
    double calcularValorInventario();
}
