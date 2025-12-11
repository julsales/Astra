package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.servicos.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Cliente
 * REFATORADO: Agora usa apenas ClienteService (sem acesso direto a repositórios)
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<ClienteService.ClienteDTO> clientes = clienteService.listarClientes();

            List<Map<String, Object>> response = clientes.stream()
                    .map(c -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", c.id());
                        map.put("nome", c.nome());
                        map.put("email", c.email());
                        map.put("tipo", c.tipo());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao listar clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/compras")
    public ResponseEntity<?> listarCompras(@PathVariable Integer id) {
        try {
            List<ClienteService.CompraDetalhadaDTO> compras = clienteService.listarComprasCliente(id);

            List<Map<String, Object>> response = compras.stream()
                    .map(this::mapearCompraParaMap)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao listar compras: " + e.getMessage()));
        }
    }

    private Map<String, Object> mapearCompraParaMap(ClienteService.CompraDetalhadaDTO compra) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", compra.id());
        map.put("clienteId", compra.clienteId());
        map.put("status", compra.status());
        map.put("pagamentoId", compra.pagamentoId());

        // Mapear ingressos
        List<Map<String, Object>> ingressos = compra.ingressos().stream()
                .map(ingresso -> {
                    Map<String, Object> ingressoMap = new HashMap<>();
                    ingressoMap.put("id", ingresso.id());
                    ingressoMap.put("qrCode", ingresso.qrCode());
                    ingressoMap.put("assento", ingresso.assento());
                    ingressoMap.put("tipo", ingresso.tipo());
                    ingressoMap.put("status", ingresso.status());
                    ingressoMap.put("sessaoId", ingresso.sessaoId());

                    if (ingresso.horario() != null) {
                        ingressoMap.put("horario", ingresso.horario());
                    }
                    if (ingresso.salaId() != null) {
                        ingressoMap.put("salaId", ingresso.salaId());
                        ingressoMap.put("sala", ingresso.sala());
                    }
                    if (ingresso.filmeTitulo() != null) {
                        ingressoMap.put("filmeTitulo", ingresso.filmeTitulo());
                    }

                    return ingressoMap;
                })
                .collect(Collectors.toList());

        map.put("ingressos", ingressos);
        map.put("quantidadeIngressos", compra.quantidadeIngressos());

        return map;
    }
}
