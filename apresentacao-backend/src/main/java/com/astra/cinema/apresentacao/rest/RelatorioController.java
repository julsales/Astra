package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.apresentacao.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/funcionario/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    /**
     * Endpoint: GET /api/funcionario/relatorios/remarcacoes
     * Retorna lista de remarcações recentes
     */
    @GetMapping("/remarcacoes")
    public ResponseEntity<List<RemarcacaoDTO>> getRemarcacoes() {
        List<RemarcacaoDTO> remarcacoes = new ArrayList<>();
        
        // TODO: Implementar lógica real com repository
        // Por enquanto, retornando lista vazia para não quebrar o frontend
        
        return ResponseEntity.ok(remarcacoes);
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/vendas
     * Retorna vendas diárias dos últimos 30 dias
     */
    @GetMapping("/vendas")
    public ResponseEntity<List<VendaDiariaDTO>> getVendas() {
        List<VendaDiariaDTO> vendas = new ArrayList<>();
        
        // TODO: Implementar lógica real com repository
        // Por enquanto, retornando lista vazia
        
        return ResponseEntity.ok(vendas);
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/filmes-populares
     * Retorna top 10 filmes mais vendidos
     */
    @GetMapping("/filmes-populares")
    public ResponseEntity<List<FilmePopularDTO>> getFilmesPopulares() {
        List<FilmePopularDTO> filmes = new ArrayList<>();
        
        // TODO: Implementar lógica real com repository
        // Por enquanto, retornando lista vazia
        
        return ResponseEntity.ok(filmes);
    }

    /**
     * Endpoint: GET /api/funcionario/relatorios/ocupacao-salas
     * Retorna ocupação atual de todas as salas
     */
    @GetMapping("/ocupacao-salas")
    public ResponseEntity<List<OcupacaoSalaDTO>> getOcupacaoSalas() {
        List<OcupacaoSalaDTO> salas = new ArrayList<>();
        
        // TODO: Implementar lógica real com repository
        // Por enquanto, retornando lista vazia
        
        return ResponseEntity.ok(salas);
    }
}
