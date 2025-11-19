package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.compra.IniciarCompraUseCase;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.compra.StatusIngresso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    private static final Logger log = LoggerFactory.getLogger(CompraController.class);

    private final IniciarCompraUseCase iniciarCompraUseCase;
    private final CompraRepositorio compraRepositorio;

    public CompraController(IniciarCompraUseCase iniciarCompraUseCase,
                           CompraRepositorio compraRepositorio) {
        this.iniciarCompraUseCase = iniciarCompraUseCase;
        this.compraRepositorio = compraRepositorio;
    }

    @PostMapping
    public ResponseEntity<?> criarCompra(@RequestBody CriarCompraRequest request) {
        // Log incoming request for debugging
        try {
            log.info("Recebendo POST /api/compras - clienteId={} sessaoId={} assentos={} tipoIngresso={}",
                request.getClienteId(), request.getSessaoId(), request.getAssentos(), request.getTipoIngresso());
        } catch (Exception e) {
            log.warn("Erro ao logar request de criarCompra", e);
        }
        try {
            // Validações
            if (request.getClienteId() == null || request.getClienteId() <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Cliente ID é obrigatório e deve ser positivo"));
            }
            if (request.getSessaoId() == null || request.getSessaoId() <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Sessão ID é obrigatório e deve ser positivo"));
            }
            if (request.getAssentos() == null || request.getAssentos().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Pelo menos um assento deve ser selecionado"));
            }

            // Cria ingressos
            // Usa um ID temporário único para cada ingresso (será substituído pelo banco)
            // Garante que seja positivo começando de 1
            AtomicInteger idTemporario = new AtomicInteger(1);
            List<Ingresso> ingressos = request.getAssentos().stream()
                .map(assento -> {
                    // Usa tipo padrão INTEIRA se não especificado
                    TipoIngresso tipo = request.getTipoIngresso() != null 
                        ? TipoIngresso.valueOf(request.getTipoIngresso().toUpperCase())
                        : TipoIngresso.INTEIRA;
                    
                    // Cria ingresso com ID temporário único (será gerado pelo banco)
                    // O QR Code será gerado automaticamente ao salvar
                    int idTemp = idTemporario.getAndIncrement();
                    return new Ingresso(
                        new IngressoId(idTemp), // ID temporário único, será substituído pelo banco
                        new SessaoId(request.getSessaoId()),
                        new AssentoId(assento),
                        tipo,
                        StatusIngresso.ATIVO,  // Inicia como ATIVO, muda para VALIDADO após scan do QR
                        null // QR Code será gerado pelo backend ao salvar
                    );
                })
                .collect(Collectors.toList());

            // DEBUG: log ingressos construídos antes de chamar o use case
            try {
                log.info("Ingressos construídos (count={}): {}", ingressos.size(), ingressos);
            } catch (Exception e) {
                log.warn("Erro ao logar ingressos construídos", e);
            }

            // Salva a compra (gera QR Codes automaticamente)
            Compra compra = iniciarCompraUseCase.executar(
                new ClienteId(request.getClienteId()),
                ingressos
            );

            // Busca a compra salva para obter os QR Codes gerados pelo backend
            Compra compraCompleta = compraRepositorio.obterPorId(compra.getCompraId());

            // Monta resposta com QR Codes
            Map<String, Object> response = new HashMap<>();
            response.put("compraId", compraCompleta.getCompraId().getId());
            response.put("clienteId", compraCompleta.getClienteId().getId());
            response.put("status", compraCompleta.getStatus().name());
            
            List<Map<String, Object>> ingressosResponse = compraCompleta.getIngressos().stream()
                .map(i -> {
                    Map<String, Object> ingressoMap = new HashMap<>();
                    ingressoMap.put("id", i.getIngressoId().getId());
                    ingressoMap.put("qrCode", i.getQrCode());
                    ingressoMap.put("codigo", i.getQrCode()); // Para compatibilidade com frontend
                    ingressoMap.put("sessaoId", i.getSessaoId().getId());
                    ingressoMap.put("assento", i.getAssentoId().getValor());
                    ingressoMap.put("tipo", i.getTipo().name());
                    ingressoMap.put("status", i.getStatus().name());
                    return ingressoMap;
                })
                .collect(Collectors.toList());
            
            response.put("ingressos", ingressosResponse);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("IllegalArgumentException ao criar compra: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("IllegalStateException ao criar compra: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("erro", "Erro interno: " + e.getMessage()));
        }
    }

    // Classe de Request
    public static class CriarCompraRequest {
        private Integer clienteId;
        private Integer sessaoId;
        private List<String> assentos;
        private String tipoIngresso; // "INTEIRA", "MEIA", "PROMOCAO"

        public Integer getClienteId() {
            return clienteId;
        }

        public void setClienteId(Integer clienteId) {
            this.clienteId = clienteId;
        }

        public Integer getSessaoId() {
            return sessaoId;
        }

        public void setSessaoId(Integer sessaoId) {
            this.sessaoId = sessaoId;
        }

        public List<String> getAssentos() {
            return assentos;
        }

        public void setAssentos(List<String> assentos) {
            this.assentos = assentos;
        }

        public String getTipoIngresso() {
            return tipoIngresso;
        }

        public void setTipoIngresso(String tipoIngresso) {
            this.tipoIngresso = tipoIngresso;
        }
    }
}

