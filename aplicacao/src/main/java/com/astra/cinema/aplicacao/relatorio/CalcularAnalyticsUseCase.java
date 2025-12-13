package com.astra.cinema.aplicacao.relatorio;

import java.util.List;
import java.util.Map;

import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

/**
 * Caso de uso para calcular analytics (vendas por dia, top produtos, inventário).
 * Segue o padrão DDD: Controller → Use Case → Repository → Infraestrutura
 */
public class CalcularAnalyticsUseCase {
    private final VendaRepositorio vendaRepositorio;
    private final ProdutoRepositorio produtoRepositorio;

    public CalcularAnalyticsUseCase(
            VendaRepositorio vendaRepositorio,
            ProdutoRepositorio produtoRepositorio) {
        this.vendaRepositorio = exigirNaoNulo(vendaRepositorio, "O repositório de vendas não pode ser nulo");
        this.produtoRepositorio = exigirNaoNulo(produtoRepositorio, "O repositório de produtos não pode ser nulo");
    }

    /**
     * Calcula vendas agrupadas por dia
     */
    public Map<String, Double> calcularVendasPorDia() {
        return vendaRepositorio.calcularVendasPorDia();
    }

    /**
     * Calcula top produtos por receita
     */
    public List<Map<String, Object>> calcularTopProdutos() {
        return vendaRepositorio.calcularTopProdutos();
    }

    /**
     * Calcula valor total do inventário (estoque * preço)
     */
    public double calcularValorInventario() {
        return produtoRepositorio.calcularValorInventario();
    }
}
