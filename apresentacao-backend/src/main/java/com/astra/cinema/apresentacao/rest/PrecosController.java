package com.astra.cinema.apresentacao.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astra.cinema.dominio.comum.PrecoIngresso;

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

        // Preços obtidos da classe centralizada PrecoIngresso
        precos.put("ingressoInteiro", PrecoIngresso.obterPrecoInteira());
        precos.put("ingressoMeia", PrecoIngresso.obterPrecoMeia());

        return ResponseEntity.ok(precos);
    }
}
