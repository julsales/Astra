package com.astra.cinema.dominio.bomboniere;

import java.util.List;

import com.astra.cinema.dominio.comum.VendaId;

public interface VendaRepositorio {
    void salvar(Venda venda);
    Venda obterPorId(VendaId vendaId);
    List<Venda> listarVendasPorStatus(StatusVenda status);
    List<Venda> buscarPorCompra(com.astra.cinema.dominio.comum.CompraId compraId);
    double calcularReceitaTotal();
}
