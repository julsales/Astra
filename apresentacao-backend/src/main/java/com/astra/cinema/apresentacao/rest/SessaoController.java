package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.servicos.SessaoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Sessão
 * REFATORADO: Agora usa apenas SessaoService (sem acesso direto a repositórios)
 */
@RestController
@RequestMapping("/api/sessoes")
@CrossOrigin(origins = "*")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    /**
     * Lista todas as sessões com filtros opcionais
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarSessoes(
            @RequestParam(required = false) Integer filmeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "false") boolean apenasAtivas) {
        try {
            List<SessaoService.SessaoDTO> sessoes = sessaoService.listarSessoes(filmeId, status, apenasAtivas);

            List<Map<String, Object>> response = sessoes.stream()
                    .map(this::mapearSessaoParaMap)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Obtém indicadores do dashboard
     */
    @GetMapping("/indicadores")
    public ResponseEntity<Map<String, Object>> obterIndicadores() {
        try {
            SessaoService.IndicadoresSessao indicadores = sessaoService.obterIndicadores();

            Map<String, Object> response = new HashMap<>();
            response.put("total", indicadores.total());
            response.put("ativas", indicadores.ativas());
            response.put("canceladas", indicadores.canceladas());
            response.put("sessoesHoje", indicadores.sessoesHoje());
            response.put("sessoesSemana", indicadores.sessoesSemana());
            response.put("ocupacaoMedia", indicadores.ocupacaoMedia());
            response.put("ingressosReservados", indicadores.ingressosReservados());
            response.put("ingressosDisponiveis", indicadores.ingressosDisponiveis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lista sessões de um filme específico
     */
    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<List<Map<String, Object>>> listarSessoesPorFilme(@PathVariable Integer filmeId) {
        try {
            List<SessaoService.SessaoDTO> sessoes = sessaoService.listarSessoesPorFilme(filmeId);
            List<Map<String, Object>> response = sessoes.stream()
                    .map(this::mapearSessaoParaMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Obtém uma sessão por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterSessao(@PathVariable Integer id) {
        try {
            SessaoService.SessaoDTO sessao = sessaoService.obterSessao(id);
            return ResponseEntity.ok(mapearSessaoParaMap(sessao));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("erro", "Sessão não encontrada"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao buscar sessão: " + e.getMessage()));
        }
    }

    /**
     * Obtém mapa de assentos disponíveis de uma sessão
     */
    @GetMapping("/{id}/assentos")
    public ResponseEntity<?> obterAssentos(@PathVariable Integer id) {
        try {
            SessaoService.AssentosInfo assentosInfo = sessaoService.obterAssentos(id);

            Map<String, Object> response = new HashMap<>();
            response.put("sessaoId", assentosInfo.sessaoId());
            response.put("assentos", assentosInfo.assentos());
            response.put("capacidade", assentosInfo.capacidade());
            response.put("disponiveis", assentosInfo.disponiveis());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("erro", "Sessão não encontrada"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao buscar assentos: " + e.getMessage()));
        }
    }

    /**
     * Reserva assentos em uma sessão
     */
    @PostMapping("/{id}/assentos/reservar")
    public ResponseEntity<?> reservarAssentos(@PathVariable Integer id,
                                              @RequestBody ReservarAssentosRequest request) {
        try {
            sessaoService.reservarAssentos(id, request.getAssentos());
            return ResponseEntity.ok(Map.of("mensagem", "Assentos reservados com sucesso"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao reservar assentos: " + e.getMessage()));
        }
    }

    /**
     * Cria uma nova sessão
     */
    @PostMapping
    public ResponseEntity<?> criarSessao(@RequestBody SessaoRequest request) {
        try {
            // Validações
            if (request.getFilmeId() == null || request.getFilmeId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Filme ID é obrigatório"));
            }
            if (request.getHorario() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Horário é obrigatório"));
            }
            if (request.getSalaId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "ID da sala é obrigatório"));
            }

            SessaoService.SessaoDTO sessao = sessaoService.criarSessao(
                    request.getFilmeId(),
                    request.getHorario(),
                    request.getSalaId()
            );

            return ResponseEntity.status(201).body(mapearSessaoParaMap(sessao));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao criar sessão: " + e.getMessage()));
        }
    }

    /**
     * Modifica uma sessão existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarSessao(@PathVariable Integer id,
                         @RequestBody SessaoRequest request) {
        try {
            SessaoService.SessaoDTO sessaoAtualizada = sessaoService.modificarSessao(
                    id,
                    request.getHorario(),
                    request.getSalaId()
            );
            return ResponseEntity.ok(mapearSessaoParaMap(sessaoAtualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao modificar sessão: " + e.getMessage()));
        }
    }

    /**
     * Remarca os ingressos de uma sessão para um novo horário
     */
    @PostMapping("/{id}/remarcar")
    public ResponseEntity<?> remarcarIngressos(@PathVariable Integer id,
                                               @RequestBody RemarcarRequest request) {
        try {
            if (request.getNovoHorario() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Novo horário é obrigatório"));
            }

            SessaoService.ResultadoRemarcacao resultado = sessaoService.remarcarIngressos(
                    id,
                    request.getNovoHorario(),
                    request.getAssentosAfetados()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", resultado.mensagem());
            response.put("ingressosRemarcados", resultado.ingressosRemarcados());
            response.put("novoHorario", resultado.novoHorario());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao remarcar ingressos: " + e.getMessage()));
        }
    }

    /**
     * Remove (cancela) uma sessão
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerSessao(@PathVariable Integer id) {
        try {
            sessaoService.removerSessao(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao remover sessão: " + e.getMessage()));
        }
    }

    /**
     * Mapeia SessaoDTO para Map
     */
    private Map<String, Object> mapearSessaoParaMap(SessaoService.SessaoDTO sessao) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", sessao.id());
        dto.put("filmeId", sessao.filmeId());
        dto.put("horario", sessao.horario());
        dto.put("status", sessao.status());
        dto.put("salaId", sessao.salaId());
        dto.put("sala", sessao.sala());
        dto.put("capacidade", sessao.capacidade());
        dto.put("filmeTitulo", sessao.filmeTitulo());
        dto.put("assentosDisponiveis", sessao.assentosDisponiveis());
        dto.put("assentosOcupados", sessao.assentosOcupados());
        dto.put("assentosReservados", sessao.assentosOcupados()); // Alias
        dto.put("ocupacao", sessao.ocupacao());

        if (sessao.filme() != null) {
            Map<String, Object> filmeInfo = new HashMap<>();
            filmeInfo.put("id", sessao.filme().id());
            filmeInfo.put("titulo", sessao.filme().titulo());
            filmeInfo.put("duracao", sessao.filme().duracao());
            filmeInfo.put("classificacaoEtaria", sessao.filme().classificacaoEtaria());
            filmeInfo.put("imagemUrl", sessao.filme().imagemUrl());
            dto.put("filme", filmeInfo);
        }

        return dto;
    }

    /**
     * Classes de Request
     */
    public static class SessaoRequest {
        private Integer filmeId;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date horario;
        private Integer capacidade;
        private Integer salaId;
        private String sala;

        public Integer getFilmeId() {
            return filmeId;
        }

        public void setFilmeId(Integer filmeId) {
            this.filmeId = filmeId;
        }

        public Date getHorario() {
            return horario;
        }

        public void setHorario(Date horario) {
            this.horario = horario;
        }

        public Integer getCapacidade() {
            return capacidade;
        }

        public Integer getSalaId() {
            return salaId;
        }

        public void setSalaId(Integer salaId) {
            this.salaId = salaId;
        }

        public void setCapacidade(Integer capacidade) {
            this.capacidade = capacidade;
        }

        public String getSala() {
            return sala;
        }

        public void setSala(String sala) {
            this.sala = sala;
        }
    }

    public static class ReservarAssentosRequest {
        private List<String> assentos;

        public List<String> getAssentos() {
            return assentos;
        }

        public void setAssentos(List<String> assentos) {
            this.assentos = assentos;
        }
    }

    public static class RemarcarRequest {
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date novoHorario;
        private String estrategia;
        private List<String> assentosAfetados;

        public Date getNovoHorario() {
            return novoHorario;
        }

        public void setNovoHorario(Date novoHorario) {
            this.novoHorario = novoHorario;
        }

        public String getEstrategia() {
            return estrategia;
        }

        public void setEstrategia(String estrategia) {
            this.estrategia = estrategia;
        }

        public List<String> getAssentosAfetados() {
            return assentosAfetados;
        }

        public void setAssentosAfetados(List<String> assentosAfetados) {
            this.assentosAfetados = assentosAfetados;
        }
    }
}
