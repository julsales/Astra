package com.astra.cinema.apresentacao.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.operacao.RemarcacaoSessao;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

@RestController
@RequestMapping("/api/funcionario/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio;
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public RelatorioController(
            RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        this.remarcacaoSessaoRepositorio = remarcacaoSessaoRepositorio;
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/remarcacoes
     * Retorna lista de remarcações recentes
     */
    @GetMapping("/remarcacoes")
    public ResponseEntity<List<Map<String, Object>>> getRemarcacoes() {
        try {
            List<RemarcacaoSessao> remarcacoes = remarcacaoSessaoRepositorio.listarTodas();
            
            List<Map<String, Object>> resultado = new ArrayList<>();
            for (RemarcacaoSessao remarcacao : remarcacoes) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", remarcacao.getRemarcacaoId() != null ? remarcacao.getRemarcacaoId().getValor() : null);
                map.put("ingressoId", remarcacao.getIngressoId().getId());
                map.put("sessaoOriginalId", remarcacao.getSessaoOriginal().getId());
                map.put("sessaoNovaId", remarcacao.getSessaoNova().getId());
                map.put("assentoOriginal", remarcacao.getAssentoOriginal().getValor());
                map.put("assentoNovo", remarcacao.getAssentoNovo().getValor());
                map.put("dataRemarcacao", remarcacao.getDataHoraRemarcacao().toString());
                map.put("motivoTecnico", remarcacao.getMotivoTecnico());
                map.put("funcionarioId", remarcacao.getFuncionarioId().getValor());
                
                resultado.add(map);
            }
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/vendas
     * Retorna estatísticas de vendas
     */
    @GetMapping("/vendas")
    public ResponseEntity<List<Map<String, Object>>> getVendas() {
        try {
            List<Compra> todasCompras = compraRepositorio.listarTodas();
            
            List<Map<String, Object>> vendas = new ArrayList<>();
            Map<String, Object> estatisticas = new HashMap<>();
            
            estatisticas.put("totalCompras", todasCompras.size());
            
            // Contar ingressos totais
            int totalIngressos = todasCompras.stream()
                .mapToInt(c -> c.getIngressos().size())
                .sum();
            estatisticas.put("totalIngressos", totalIngressos);
            
            // Contar por status
            long comprasConfirmadas = todasCompras.stream()
                .filter(c -> c.getStatus() == com.astra.cinema.dominio.compra.StatusCompra.CONFIRMADA)
                .count();
            estatisticas.put("comprasConfirmadas", comprasConfirmadas);
            
            long comprasPendentes = todasCompras.stream()
                .filter(c -> c.getStatus() == com.astra.cinema.dominio.compra.StatusCompra.PENDENTE)
                .count();
            estatisticas.put("comprasPendentes", comprasPendentes);
            
            vendas.add(estatisticas);
            
            return ResponseEntity.ok(vendas);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/filmes-populares
     * Retorna top 10 filmes mais vendidos
     */
    @GetMapping("/filmes-populares")
    public ResponseEntity<List<Map<String, Object>>> getFilmesPopulares() {
        try {
            List<Compra> todasCompras = compraRepositorio.listarTodas();
            
            // Contar ingressos por filme e calcular receita
            Map<Integer, Integer> ingressosPorFilme = new HashMap<>();
            Map<Integer, Double> receitaPorFilme = new HashMap<>();
            Map<Integer, String> nomesFilmes = new HashMap<>();
            
            for (Compra compra : todasCompras) {
                for (Ingresso ingresso : compra.getIngressos()) {
                    try {
                        Sessao sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
                        if (sessao != null) {
                            int filmeId = sessao.getFilmeId().getId();
                            ingressosPorFilme.put(filmeId, ingressosPorFilme.getOrDefault(filmeId, 0) + 1);
                            
                            // Calcular receita do ingresso (R$ 25 inteira, R$ 12.50 meia)
                            double precoIngresso = ingresso.getTipo() == com.astra.cinema.dominio.compra.TipoIngresso.INTEIRA ? 25.0 : 12.5;
                            receitaPorFilme.put(filmeId, receitaPorFilme.getOrDefault(filmeId, 0.0) + precoIngresso);
                            
                            if (!nomesFilmes.containsKey(filmeId)) {
                                Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
                                if (filme != null) {
                                    nomesFilmes.put(filmeId, filme.getTitulo());
                                }
                            }
                        }
                    } catch (Exception e) {}
                }
            }
            
            // Criar lista ordenada
            List<Map<String, Object>> filmes = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : ingressosPorFilme.entrySet()) {
                Map<String, Object> filme = new HashMap<>();
                filme.put("filmeId", entry.getKey());
                filme.put("titulo", nomesFilmes.getOrDefault(entry.getKey(), "Filme Desconhecido"));
                filme.put("totalIngressos", entry.getValue());
                filme.put("receita", Math.round(receitaPorFilme.getOrDefault(entry.getKey(), 0.0) * 100) / 100.0);
                filmes.add(filme);
            }
            
            // Ordenar por quantidade
            filmes.sort((a, b) -> ((Integer)b.get("totalIngressos")).compareTo((Integer)a.get("totalIngressos")));
            
            return ResponseEntity.ok(filmes.stream().limit(10).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/ocupacao-salas
     * Retorna ocupação atual de todas as salas
     */
    @GetMapping("/ocupacao-salas")
    public ResponseEntity<List<Map<String, Object>>> getOcupacaoSalas() {
        try {
            List<Sessao> todasSessoes = sessaoRepositorio.listarTodas();
            
            // Agrupar por sala
            Map<Integer, List<Sessao>> sessoesPorSala = todasSessoes.stream()
                .collect(Collectors.groupingBy(s -> s.getSalaId().getId()));
            
            List<Map<String, Object>> salas = new ArrayList<>();
            for (Map.Entry<Integer, List<Sessao>> entry : sessoesPorSala.entrySet()) {
                Map<String, Object> sala = new HashMap<>();
                sala.put("salaId", entry.getKey());
                sala.put("totalSessoes", entry.getValue().size());
                
                // Calcular ocupação média
                int totalAssentos = 0;
                int assentosOcupados = 0;
                
                for (Sessao sessao : entry.getValue()) {
                    Map<com.astra.cinema.dominio.comum.AssentoId, Boolean> assentos = sessao.getMapaAssentosDisponiveis();
                    totalAssentos += assentos.size();
                    assentosOcupados += (int) assentos.values().stream().filter(ocupado -> !ocupado).count();
                }
                
                double taxaOcupacao = totalAssentos > 0 ? (assentosOcupados * 100.0 / totalAssentos) : 0;
                sala.put("taxaOcupacao", Math.round(taxaOcupacao * 100) / 100.0);
                sala.put("totalAssentos", totalAssentos);
                sala.put("assentosOcupados", assentosOcupados);
                
                salas.add(sala);
            }
            
            // Ordenar por sala
            salas.sort((a, b) -> ((Integer)a.get("salaId")).compareTo((Integer)b.get("salaId")));
            
            return ResponseEntity.ok(salas);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
