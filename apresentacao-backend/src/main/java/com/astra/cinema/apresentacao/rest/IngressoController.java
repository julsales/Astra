package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.ingresso.RemarcarIngressoUseCase;
import com.astra.cinema.aplicacao.ingresso.ValidarIngressoUseCase;
import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingressos")
@CrossOrigin(origins = "*")
public class IngressoController {

    private final ValidarIngressoUseCase validarIngressoUseCase;
    private final RemarcarIngressoUseCase remarcarIngressoUseCase;
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public IngressoController(ValidarIngressoUseCase validarIngressoUseCase,
                             RemarcarIngressoUseCase remarcarIngressoUseCase,
                             CompraRepositorio compraRepositorio,
                             SessaoRepositorio sessaoRepositorio,
                             FilmeRepositorio filmeRepositorio) {
        this.validarIngressoUseCase = validarIngressoUseCase;
        this.remarcarIngressoUseCase = remarcarIngressoUseCase;
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validarIngresso(@RequestBody ValidarRequest request) {
        try {
            var resultado = validarIngressoUseCase.executar(request.getQrCode());

            Map<String, Object> response = new HashMap<>();
            response.put("valido", resultado.isValido());
            response.put("mensagem", resultado.getMensagem());

            if (resultado.getIngresso() != null) {
                Ingresso ingressoValidado = resultado.getIngresso();

                // Buscar a compra completa para obter TODOS os assentos
                var compra = compraRepositorio.buscarCompraPorQrCode(request.getQrCode());

                // Coletar todos os assentos da compra
                String todosAssentos = compra != null && compra.getIngressos() != null
                    ? compra.getIngressos().stream()
                        .map(i -> i.getAssentoId().getValor())
                        .collect(Collectors.joining(", "))
                    : ingressoValidado.getAssentoId().getValor();

                // Verifica se o ingresso foi validado por funcionário (status == VALIDADO)
                boolean foiValidado = ingressoValidado.getStatus() == StatusIngresso.VALIDADO;

                response.put("ingresso", Map.of(
                    "id", ingressoValidado.getIngressoId().getId(),
                    "qrCode", ingressoValidado.getQrCode(),
                    "tipo", ingressoValidado.getTipo().name(),
                    "status", ingressoValidado.getStatus().name(),
                    "assento", todosAssentos,  // TODOS os assentos da compra
                    "assentoIndividual", ingressoValidado.getAssentoId().getValor(),  // Assento deste ingresso específico
                    "foiValidado", foiValidado
                ));
            }

            if (resultado.getSessao() != null) {
                response.put("sessao", Map.of(
                    "id", resultado.getSessao().getSessaoId().getId(),
                    "horario", resultado.getSessao().getHorario().toString(),
                    "salaId", resultado.getSessao().getSalaId().getId(),
                    "sala", "Sala " + resultado.getSessao().getSalaId().getId()
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
            remarcarIngressoUseCase.executar(
                request.getQrCode(),
                new SessaoId(request.getNovaSessaoId()),
                new AssentoId(request.getNovoAssentoId())
            );
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Ingresso remarcado com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/ativos")
    public ResponseEntity<?> buscarIngressosAtivos() {
        try {
            List<Ingresso> ingressos = compraRepositorio.buscarIngressosAtivos();

            List<Map<String, Object>> response = ingressos.stream()
                .map(i -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", i.getIngressoId().getId());
                    map.put("qrCode", i.getQrCode());
                    map.put("sessaoId", i.getSessaoId().getId());

                    // Buscar TODOS os assentos da compra (não apenas deste ingresso)
                    try {
                        var compra = compraRepositorio.buscarCompraPorQrCode(i.getQrCode());
                        if (compra != null && compra.getIngressos() != null) {
                            String todosAssentos = compra.getIngressos().stream()
                                .map(ing -> ing.getAssentoId().getValor())
                                .collect(Collectors.joining(", "));
                            map.put("assento", todosAssentos);  // TODOS os assentos
                        } else {
                            map.put("assento", i.getAssentoId().getValor());  // Fallback
                        }
                    } catch (Exception e) {
                        map.put("assento", i.getAssentoId().getValor());  // Fallback
                    }

                    map.put("assentoIndividual", i.getAssentoId().getValor());  // Assento individual
                    map.put("tipo", i.getTipo().name());
                    map.put("status", i.getStatus().name());

                    // Adicionar flag foiValidado (true se status é VALIDADO)
                    boolean foiValidado = i.getStatus() == StatusIngresso.VALIDADO;
                    map.put("foiValidado", foiValidado);

                    // Buscar informações da sessão
                    try {
                        Sessao sessao = sessaoRepositorio.obterPorId(i.getSessaoId());
                        if (sessao != null) {
                            map.put("horario", sessao.getHorario().toString());
                            map.put("salaId", sessao.getSalaId().getId());
                            map.put("sala", "Sala " + sessao.getSalaId().getId());

                            // Buscar informações do filme
                            try {
                                Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
                                if (filme != null) {
                                    Map<String, Object> filmeMap = new HashMap<>();
                                    filmeMap.put("id", filme.getFilmeId().getId());
                                    filmeMap.put("titulo", filme.getTitulo());
                                    filmeMap.put("sinopse", filme.getSinopse());
                                    filmeMap.put("classificacaoEtaria", filme.getClassificacaoEtaria());
                                    filmeMap.put("duracao", filme.getDuracao());
                                    map.put("filme", filmeMap);
                                }
                            } catch (Exception e) {
                                // Se não encontrar filme, continua sem adicionar
                            }
                        }
                    } catch (Exception e) {
                        // Se não encontrar sessão, continua sem adicionar
                    }

                    return map;
                })
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
            // Buscar todos os ingressos (não apenas ativos)
            List<Map<String, Object>> historico = compraRepositorio.buscarIngressosAtivos().stream()
                .map(i -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", i.getIngressoId().getId());
                    map.put("qrCode", i.getQrCode());
                    map.put("status", i.getStatus().name());
                    map.put("sessaoId", i.getSessaoId().getId());
                    map.put("assento", i.getAssentoId().getValor());
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
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
