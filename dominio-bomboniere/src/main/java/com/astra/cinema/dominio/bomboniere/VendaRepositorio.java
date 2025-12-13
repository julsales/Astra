package com.astra.cinema.dominio.bomboniere;

import java.util.List;
import java.util.Map;

import com.astra.cinema.dominio.comum.VendaId;

public interface VendaRepositorio {
    void salvar(Venda venda);
    Venda obterPorId(VendaId vendaId);
    List<Venda> listarVendasPorStatus(StatusVenda status);
    List<Venda> buscarPorCompra(com.astra.cinema.dominio.comum.CompraId compraId);
    double calcularReceitaTotal();
    Map<String, Double> calcularVendasPorDia();
    List<Map<String, Object>> calcularTopProdutos();
}
