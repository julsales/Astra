package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.dominio.sessao.Sala;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar Salas de Cinema
 */
@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "*")
public class SalaController {

    private final com.astra.cinema.aplicacao.servicos.SalaService salaService;

    @Autowired
    public SalaController(com.astra.cinema.aplicacao.servicos.SalaService salaService) {
        this.salaService = salaService;
    }

    /**
     * Lista todas as salas disponíveis
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarSalas() {
        try {
            List<Sala> salas = salaService.listarTodasSalas();
            
            List<Map<String, Object>> salasDTO = salas.stream()
                .map(this::mapearSalaParaDTO)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(salasDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Busca uma sala por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterSala(@PathVariable Integer id) {
        try {
            Sala sala = salaService.obterSalaPorId(new com.astra.cinema.dominio.comum.SalaId(id));
            
            if (sala == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(mapearSalaParaDTO(sala));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Mapeia Sala de domínio para DTO
     */
    private Map<String, Object> mapearSalaParaDTO(Sala sala) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", sala.getSalaId().getId());
        dto.put("nome", sala.getNome());
        dto.put("capacidade", sala.getCapacidade());
        dto.put("tipo", sala.getTipo().name());
        dto.put("tipoDescricao", sala.getTipo().getDescricao());
        return dto;
    }
}
