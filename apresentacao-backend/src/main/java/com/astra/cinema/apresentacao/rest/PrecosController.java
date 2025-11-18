package com.astra.cinema.apresentacao.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para preços de ingressos e produtos
 * Endpoint centralizado para configuração de preços da aplicação
 */
@RestController
@RequestMapping("/api/precos")
@CrossOrigin(origins = "*")
public class PrecosController {

    /**
     * Retorna os preços configurados para ingressos
     *
     * @return Map com preços de ingresso inteiro e meia
     */
    @GetMapping
    public ResponseEntity<Map<String, Double>> obterPrecos() {
        Map<String, Double> precos = new HashMap<>();

        // Preços configurados (futuramente podem vir de configuração ou banco)
        precos.put("ingressoInteiro", 35.00);
        precos.put("ingressoMeia", 17.50);

        return ResponseEntity.ok(precos);
    }
}
