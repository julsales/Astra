package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.servicos.IngressoService;
import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.SessaoId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Ingresso
 * REFATORADO: Agora usa apenas IngressoService (sem acesso direto a repositórios)
 */
@RestController
@RequestMapping("/api/ingressos")
@CrossOrigin(origins = "*")
public class IngressoController {
    private static final Logger logger = LoggerFactory.getLogger(IngressoController.class);

    private final IngressoService ingressoService;

    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validarIngresso(@RequestBody ValidarRequest request) {
        try {
            var resultado = ingressoService.validarIngresso(request.getQrCode());

            Map<String, Object> response = new HashMap<>();
            response.put("valido", resultado.valido());
            response.put("mensagem", resultado.mensagem());

            if (resultado.ingresso() != null) {
                boolean foiValidado = resultado.ingresso().getStatus() ==
                    com.astra.cinema.dominio.compra.StatusIngresso.VALIDADO;

                response.put("ingresso", Map.of(
                    "id", resultado.ingresso().getIngressoId().getId(),
                    "qrCode", resultado.ingresso().getQrCode(),
                    "tipo", resultado.ingresso().getTipo().name(),
                    "status", resultado.ingresso().getStatus().name(),
                    "assento", resultado.todosAssentos(),
                    "assentoIndividual", resultado.ingresso().getAssentoId().getValor(),
                    "foiValidado", foiValidado
                ));
            }

            if (resultado.sessao() != null) {
                response.put("sessao", Map.of(
                    "id", resultado.sessao().getSessaoId().getId(),
                    "horario", resultado.sessao().getHorario().toString(),
                    "salaId", resultado.sessao().getSalaId().getId(),
                    "sala", "Sala " + resultado.sessao().getSalaId().getId()
                ));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/remarcar")
    public ResponseEntity<?> remarcarIngresso(@RequestBody RemarcarRequest request) {
        try {
            logger.info("Recebida solicitação de remarcação. QRCode: {}, Nova Sessão: {}, Novo Assento: {}",
                request.getQrCode(), request.getNovaSessaoId(), request.getNovoAssentoId());

            ingressoService.remarcarIngresso(
                request.getQrCode(),
                new SessaoId(request.getNovaSessaoId()),
                new AssentoId(request.getNovoAssentoId())
            );

            logger.info("Ingresso remarcado com sucesso. QRCode: {}", request.getQrCode());

            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Ingresso remarcado com sucesso"
            ));
        } catch (Exception e) {
            logger.error("Erro ao remarcar ingresso: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> buscarIngressos(@RequestParam(required = false) Integer clienteId) {
        try {
            logger.info("Buscando ingressos para cliente: {}", clienteId);

            List<IngressoService.IngressoDetalhado> ingressos =
                ingressoService.buscarIngressosPorCliente(clienteId);

            List<Map<String, Object>> response = ingressos.stream()
                .map(this::converterParaMap)
                .collect(Collectors.toList());

            logger.info("Retornando {} grupos de ingressos", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao buscar ingressos: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/ativos")
    public ResponseEntity<?> buscarIngressosAtivos(@RequestParam(required = false) Integer clienteId) {
        try {
            List<IngressoService.IngressoDetalhado> ingressos =
                ingressoService.buscarIngressosAtivosPorCliente(clienteId);

            List<Map<String, Object>> response = ingressos.stream()
                .map(this::converterParaMap)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/historico")
    public ResponseEntity<?> buscarHistorico() {
        try {
            // Por enquanto retorna lista vazia - TODO: implementar no serviço se necessário
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    private Map<String, Object> converterParaMap(IngressoService.IngressoDetalhado ingresso) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ingresso.id());
        map.put("qrCode", ingresso.qrCode());
        map.put("codigo", ingresso.qrCode());
        map.put("sessaoId", ingresso.sessaoId());
        map.put("assento", ingresso.assento());
        map.put("assentos", ingresso.assentos());
        map.put("assentoIndividual", ingresso.assentoIndividual());
        map.put("tipo", ingresso.tipo());
        map.put("status", ingresso.status());
        map.put("foiValidado", ingresso.foiValidado());
        map.put("remarcado", ingresso.remarcado());
        map.put("total", ingresso.total());

        if (ingresso.horario() != null) {
            map.put("horario", ingresso.horario());
        }
        if (ingresso.salaId() != null) {
            map.put("salaId", ingresso.salaId());
            map.put("sala", ingresso.sala());
        }

        if (ingresso.filmeId() != null) {
            Map<String, Object> filmeMap = new HashMap<>();
            filmeMap.put("id", ingresso.filmeId());
            filmeMap.put("titulo", ingresso.filmeTitulo());
            filmeMap.put("sinopse", ingresso.filmeSinopse());
            filmeMap.put("classificacaoEtaria", ingresso.filmeClassificacaoEtaria());
            filmeMap.put("duracao", ingresso.filmeDuracao());
            map.put("filme", filmeMap);
        }

        // Ingressos detalhados
        List<Map<String, String>> ingressosDetalhados = ingresso.ingressosDetalhados().stream()
            .map(ing -> {
                Map<String, String> ingressoDetalhe = new HashMap<>();
                ingressoDetalhe.put("assento", ing.assento());
                ingressoDetalhe.put("tipo", ing.tipo());
                return ingressoDetalhe;
            })
            .collect(Collectors.toList());
        map.put("ingressosDetalhados", ingressosDetalhados);

        // Produtos
        List<Map<String, Object>> produtos = ingresso.produtos().stream()
            .map(p -> {
                Map<String, Object> produtoMap = new HashMap<>();
                produtoMap.put("id", p.id());
                produtoMap.put("nome", p.nome());
                produtoMap.put("preco", p.preco());
                produtoMap.put("quantidade", p.quantidade());
                return produtoMap;
            })
            .collect(Collectors.toList());
        map.put("produtos", produtos);

        // Histórico de remarcação
        if (ingresso.historicoRemarcacao() != null) {
            var historico = ingresso.historicoRemarcacao();
            Map<String, Object> historicoMap = new HashMap<>();
            historicoMap.put("dataRemarcacao", historico.dataRemarcacao());
            historicoMap.put("motivo", historico.motivo());

            if (historico.assentoOriginal() != null) {
                historicoMap.put("assentoOriginal", historico.assentoOriginal());
            }

            if (historico.sessaoOriginalHorario() != null) {
                Map<String, Object> sessaoOriginalMap = new HashMap<>();
                sessaoOriginalMap.put("horario", historico.sessaoOriginalHorario());
                sessaoOriginalMap.put("sala", historico.sessaoOriginalSala());
                sessaoOriginalMap.put("filme", historico.sessaoOriginalFilme());
                historicoMap.put("sessaoOriginal", sessaoOriginalMap);
            }

            map.put("historicoRemarcacao", historicoMap);
        }

        return map;
    }

    // Classes de Request
    public static class ValidarRequest {
        private String qrCode;

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }
    }

    public static class RemarcarRequest {
        private String qrCode;
        private int novaSessaoId;
        private String novoAssentoId;

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public int getNovaSessaoId() {
            return novaSessaoId;
        }

        public void setNovaSessaoId(int novaSessaoId) {
            this.novaSessaoId = novaSessaoId;
        }

        public String getNovoAssentoId() {
            return novoAssentoId;
        }

        public void setNovoAssentoId(String novoAssentoId) {
            this.novoAssentoId = novoAssentoId;
        }
    }
}
