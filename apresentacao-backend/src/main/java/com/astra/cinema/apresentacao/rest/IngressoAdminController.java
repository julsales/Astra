package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.servicos.IngressoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/ingressos")
@CrossOrigin(origins = "*")
public class IngressoAdminController {
    
    private final IngressoService ingressoService;

    public IngressoAdminController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    /**
     * Expira manualmente todos os ingressos ativos de sessões que já passaram
     */
    @PostMapping("/expirar")
    public ResponseEntity<?> expirarIngressos() {
        try {
            int quantidadeExpirada = ingressoService.expirarTodosIngressosExpirados();
            return ResponseEntity.ok(Map.of(
                "mensagem", "Ingressos expirados com sucesso",
                "quantidade", quantidadeExpirada
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("erro", "Erro ao expirar ingressos: " + e.getMessage()));
        }
    }
}
