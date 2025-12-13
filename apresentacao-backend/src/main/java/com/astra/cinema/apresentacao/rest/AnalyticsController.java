package com.astra.cinema.apresentacao.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astra.cinema.aplicacao.relatorio.CalcularAnalyticsUseCase;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final CalcularAnalyticsUseCase calcularAnalyticsUseCase;

    public AnalyticsController(CalcularAnalyticsUseCase calcularAnalyticsUseCase) {
        this.calcularAnalyticsUseCase = calcularAnalyticsUseCase;
    }

    // Vendas agrupadas por dia (yyyy-MM-dd) - inclui apenas vendas confirmadas
    @GetMapping("/vendas-por-dia")
    public List<Map<String, Object>> vendasPorDia() {
        Map<String, Double> somaPorDia = calcularAnalyticsUseCase.calcularVendasPorDia();
        
        List<Map<String, Object>> resp = new ArrayList<>();
        for (Map.Entry<String, Double> e : somaPorDia.entrySet()) {
            resp.add(Map.of("date", e.getKey(), "total", e.getValue()));
        }
        return resp;
    }

    // Top produtos por receita
    @GetMapping("/top-produtos")
    public List<Map<String, Object>> topProdutos() {
        return calcularAnalyticsUseCase.calcularTopProdutos();
    }

    // Valor total do inventário (estoque * preco)
    @GetMapping("/inventario-valor")
    public Map<String, Object> inventarioValor() {
        double total = calcularAnalyticsUseCase.calcularValorInventario();
        return Map.of("total", total);
    }
}
