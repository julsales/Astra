package com.astra.cinema.dominio.bomboniere;

import com.astra.cinema.dominio.comum.ProdutoId;

public interface ProdutoRepositorio {
    void salvar(Produto produto);
    Produto obterPorId(ProdutoId produtoId);
}
