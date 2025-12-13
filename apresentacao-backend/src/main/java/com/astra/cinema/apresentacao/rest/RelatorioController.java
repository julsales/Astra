package com.astra.cinema.apresentacao.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astra.cinema.aplicacao.relatorio.CalcularFilmesPopularesUseCase;
import com.astra.cinema.aplicacao.relatorio.CalcularOcupacaoSalasUseCase;
import com.astra.cinema.aplicacao.relatorio.CalcularRelatorioVendasUseCase;
import com.astra.cinema.dominio.operacao.RemarcacaoSessao;

@RestController
@RequestMapping("/api/funcionario/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final com.astra.cinema.aplicacao.relatorio.ListarRemarcacoesUseCase listarRemarcacoesUseCase;
    private final CalcularRelatorioVendasUseCase calcularRelatorioVendasUseCase;
    private final CalcularFilmesPopularesUseCase calcularFilmesPopularesUseCase;
    private final CalcularOcupacaoSalasUseCase calcularOcupacaoSalasUseCase;

    public RelatorioController(
            com.astra.cinema.aplicacao.relatorio.ListarRemarcacoesUseCase listarRemarcacoesUseCase,
            CalcularRelatorioVendasUseCase calcularRelatorioVendasUseCase,
            CalcularFilmesPopularesUseCase calcularFilmesPopularesUseCase,
            CalcularOcupacaoSalasUseCase calcularOcupacaoSalasUseCase) {
        this.listarRemarcacoesUseCase = listarRemarcacoesUseCase;
        this.calcularRelatorioVendasUseCase = calcularRelatorioVendasUseCase;
        this.calcularFilmesPopularesUseCase = calcularFilmesPopularesUseCase;
        this.calcularOcupacaoSalasUseCase = calcularOcupacaoSalasUseCase;
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/remarcacoes
     * Retorna lista de remarcações recentes
     */
    @GetMapping("/remarcacoes")
    public ResponseEntity<List<Map<String, Object>>> getRemarcacoes() {
        try {
            List<RemarcacaoSessao> remarcacoes = listarRemarcacoesUseCase.executar();
            
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
            Map<String, Object> estatisticas = calcularRelatorioVendasUseCase.executar();
            List<Map<String, Object>> vendas = new ArrayList<>();
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
            List<Map<String, Object>> filmes = calcularFilmesPopularesUseCase.executar();
            return ResponseEntity.ok(filmes);
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
        return ResponseEntity.ok(calcularOcupacaoSalasUseCase.executar());
    }
}
