package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.infraestrutura.persistencia.jpa.ProdutoJpaRepository;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpaRepository;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpa;
import com.astra.cinema.infraestrutura.persistencia.jpa.ProdutoJpa;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final VendaJpaRepository vendaRepo;
    private final ProdutoJpaRepository produtoRepo;

    public AnalyticsController(VendaJpaRepository vendaRepo, ProdutoJpaRepository produtoRepo) {
        this.vendaRepo = vendaRepo;
        this.produtoRepo = produtoRepo;
    }

    // Vendas agrupadas por dia (yyyy-MM-dd) - inclui apenas vendas confirmadas
    @GetMapping("/vendas-por-dia")
    public List<Map<String, Object>> vendasPorDia() {
        List<VendaJpa> vendas = vendaRepo.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Double> somaPorDia = new TreeMap<>();

        // Cada VendaJpa corresponde a uma linha de venda (um produto). Calculamos preço por produto
        Map<Integer, ProdutoJpa> produtoCache = produtoRepo.findAll().stream().collect(Collectors.toMap(ProdutoJpa::getId, p -> p));

        for (VendaJpa v : vendas) {
            if (v.getCriadoEm() == null) continue;
            String day = v.getCriadoEm().format(fmt);
            ProdutoJpa p = produtoCache.get(v.getProdutoId());
            double preco = p != null ? (p.getPreco() != null ? p.getPreco() : 0.0) : 0.0;
            double valor = preco * (v.getQuantidade() != null ? v.getQuantidade() : 1);
            somaPorDia.put(day, somaPorDia.getOrDefault(day, 0.0) + valor);
        }

        // Convert to list of {date, total}
        List<Map<String, Object>> resp = new ArrayList<>();
        for (Map.Entry<String, Double> e : somaPorDia.entrySet()) {
            resp.add(Map.of("date", e.getKey(), "total", e.getValue()));
        }
        return resp;
    }

    // Top produtos por receita
    @GetMapping("/top-produtos")
    public List<Map<String, Object>> topProdutos() {
        List<VendaJpa> vendas = vendaRepo.findAll();
        Map<Integer, Double> receitaPorProduto = new HashMap<>();

        Map<Integer, ProdutoJpa> produtoCache = produtoRepo.findAll().stream().collect(Collectors.toMap(ProdutoJpa::getId, p -> p));

        for (VendaJpa v : vendas) {
            ProdutoJpa p = produtoCache.get(v.getProdutoId());
            double preco = p != null ? (p.getPreco() != null ? p.getPreco() : 0.0) : 0.0;
            double valor = preco * (v.getQuantidade() != null ? v.getQuantidade() : 1);
            receitaPorProduto.put(v.getProdutoId(), receitaPorProduto.getOrDefault(v.getProdutoId(), 0.0) + valor);
        }

        // Build list
        List<Map<String, Object>> lista = receitaPorProduto.entrySet().stream()
            .map(e -> {
                ProdutoJpa p = produtoCache.get(e.getKey());
                String nome = p != null ? p.getNome() : String.valueOf(e.getKey());
                Map<String, Object> m = new HashMap<>();
                m.put("produtoId", e.getKey());
                m.put("nome", nome);
                m.put("receita", e.getValue());
                return m;
            })
            .sorted((a,b) -> Double.compare(((Number)b.get("receita")).doubleValue(), ((Number)a.get("receita")).doubleValue()))
            .collect(Collectors.toList());

        return lista;
    }

    // Valor total do inventário (estoque * preco) e lista de produtos com valor
    @GetMapping("/inventario-valor")
    public Map<String, Object> inventarioValor() {
        List<ProdutoJpa> produtos = produtoRepo.findAll();
        double total = 0.0;
        List<Map<String, Object>> items = new ArrayList<>();
        for (ProdutoJpa p : produtos) {
            double preco = p.getPreco() != null ? p.getPreco() : 0.0;
            int estoque = p.getEstoque() != null ? p.getEstoque() : 0;
            double valor = preco * estoque;
            total += valor;
            items.add(Map.of("produtoId", p.getId(), "nome", p.getNome(), "estoque", estoque, "valor", valor));
        }
        return Map.of("total", total, "items", items);
    }
}
