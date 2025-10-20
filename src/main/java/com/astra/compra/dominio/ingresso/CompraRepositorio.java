package com.astra.compra.dominio.ingresso;

import java.util.List;

public interface CompraRepositorio {
    Compra salvar(Compra compra);
    Compra obter(CompraId id);
    List<Compra> buscarTodas();
}
