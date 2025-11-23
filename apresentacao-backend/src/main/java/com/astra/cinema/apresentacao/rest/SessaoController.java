package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.sessao.*;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.filme.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Sessão
 * Padrão: Front Controller (Spring MVC)
 */
@RestController
@RequestMapping("/api/sessoes")
@CrossOrigin(origins = "*")
public class SessaoController {

    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final CriarSessaoUseCase criarSessaoUseCase;
    private final ModificarSessaoUseCase modificarSessaoUseCase;
    private final RemoverSessaoUseCase removerSessaoUseCase;
    private final RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase;

    public SessaoController(SessaoRepositorio sessaoRepositorio,
                           FilmeRepositorio filmeRepositorio,
                           CriarSessaoUseCase criarSessaoUseCase,
                           ModificarSessaoUseCase modificarSessaoUseCase,
                           RemoverSessaoUseCase removerSessaoUseCase,
                           RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase) {
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.criarSessaoUseCase = criarSessaoUseCase;
        this.modificarSessaoUseCase = modificarSessaoUseCase;
        this.removerSessaoUseCase = removerSessaoUseCase;
        this.remarcarIngressosSessaoUseCase = remarcarIngressosSessaoUseCase;
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
            List<Sessao> sessoes;

            if (filmeId != null) {
                sessoes = sessaoRepositorio.buscarPorFilme(new FilmeId(filmeId));
            } else {
                sessoes = sessaoRepositorio.listarTodas();
            }

            // Filtro por status
            if (status != null && !status.isEmpty()) {
                StatusSessao statusEnum = StatusSessao.valueOf(status.toUpperCase());
                sessoes = sessoes.stream()
                        .filter(s -> s.getStatus() == statusEnum)
                        .collect(Collectors.toList());
            }

            // Filtro apenas ativas (DISPONIVEL ou ESGOTADA, mas não CANCELADA)
            if (apenasAtivas) {
                sessoes = sessoes.stream()
                        .filter(s -> s.getStatus() != StatusSessao.CANCELADA)
                        .collect(Collectors.toList());
            }

            List<Map<String, Object>> response = sessoes.stream()
                    .map(this::mapearSessaoParaDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Obtém indicadores do dashboard (total de sessões, ocupação média, etc.)
     */
    @GetMapping("/indicadores")
    public ResponseEntity<Map<String, Object>> obterIndicadores() {
        try {
            List<Sessao> todasSessoes = sessaoRepositorio.listarTodas();

            long totalSessoes = todasSessoes.size();
            long sessoesDisponiveis = todasSessoes.stream()
                    .filter(s -> s.getStatus() == StatusSessao.DISPONIVEL)
                    .count();
            long sessoesEsgotadas = todasSessoes.stream()
                    .filter(s -> s.getStatus() == StatusSessao.ESGOTADA)
                    .count();

            // Calcula ocupação média
            double ocupacaoMedia = todasSessoes.stream()
                    .mapToDouble(this::calcularOcupacao)
                    .average()
                    .orElse(0.0);

        Map<String, Object> indicadores = new HashMap<>();
        // Campos compatíveis com o frontend
        indicadores.put("total", totalSessoes);
        indicadores.put("ativas", sessoesDisponiveis);
        indicadores.put("canceladas", sessoesEsgotadas);

        // Sessões hoje
        long sessoesHoje = todasSessoes.stream()
            .filter(s -> {
            Calendar now = Calendar.getInstance();
            Calendar sh = Calendar.getInstance();
            sh.setTime(s.getHorario());
            return now.get(Calendar.YEAR) == sh.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == sh.get(Calendar.DAY_OF_YEAR);
            }).count();

        // Sessões esta semana
        long sessoesSemana = todasSessoes.stream()
            .filter(s -> {
            Calendar now = Calendar.getInstance();
            Calendar st = Calendar.getInstance();
            st.setTime(s.getHorario());
            return now.get(Calendar.YEAR) == st.get(Calendar.YEAR)
                && now.get(Calendar.WEEK_OF_YEAR) == st.get(Calendar.WEEK_OF_YEAR);
            }).count();

        indicadores.put("sessoesHoje", sessoesHoje);
        indicadores.put("sessoesSemana", sessoesSemana);
        indicadores.put("ocupacaoMedia", Math.round(ocupacaoMedia * 100.0) / 100.0);

        long ingressosReservados = todasSessoes.stream()
            .mapToLong(s -> {
            long disponiveis = s.getMapaAssentosDisponiveis().values().stream().filter(d -> d).count();
            return s.getCapacidade() - disponiveis;
            }).sum();

        long ingressosDisponiveis = todasSessoes.stream()
            .mapToLong(Sessao::getCapacidade)
            .sum() - ingressosReservados;

        indicadores.put("ingressosReservados", ingressosReservados);
        indicadores.put("ingressosDisponiveis", Math.max(0L, ingressosDisponiveis));

        return ResponseEntity.ok(indicadores);
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
            List<Sessao> sessoes = sessaoRepositorio.buscarPorFilme(new FilmeId(filmeId));
            List<Map<String, Object>> response = sessoes.stream()
                    .map(this::mapearSessaoParaDTO)
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
            Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
            if (sessao == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Sessão não encontrada"));
            }
            return ResponseEntity.ok(mapearSessaoParaDTO(sessao));
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
            Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
            if (sessao == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Sessão não encontrada"));
            }

            Map<String, Boolean> assentosMap = new HashMap<>();
            sessao.getMapaAssentosDisponiveis().forEach((assentoId, disponivel) -> {
                assentosMap.put(assentoId.getValor(), disponivel);
            });

            Map<String, Object> response = new HashMap<>();
            response.put("sessaoId", id);
            response.put("assentos", assentosMap);
            response.put("capacidade", sessao.getCapacidade());
            response.put("disponiveis", assentosMap.values().stream().filter(d -> d).count());

            return ResponseEntity.ok(response);
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
            Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
            if (sessao == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Sessão não encontrada"));
            }

            // Reserva cada assento
            for (String assentoStr : request.getAssentos()) {
                AssentoId assentoId = new AssentoId(assentoStr);
                sessao.reservarAssento(assentoId);
            }

            // Salva a sessão atualizada
            sessaoRepositorio.salvar(sessao);

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

            int capacidade = request.getCapacidade() != null && request.getCapacidade() > 0 ?
                    request.getCapacidade() : 50;
            String sala = request.getSala() != null && !request.getSala().trim().isEmpty() ?
                    request.getSala() : "Sala 1";

            Sessao sessao = criarSessaoUseCase.executar(
                    new FilmeId(request.getFilmeId()),
                    request.getHorario(),
                    capacidade,
                    sala
            );

            return ResponseEntity.status(201).body(mapearSessaoParaDTO(sessao));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (IllegalArgumentException e) {
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
        Date novoHorario = request.getHorario();
        String novaSala = request.getSala();
        Integer novaCapacidade = request.getCapacidade();

        modificarSessaoUseCase.executar(
            new SessaoId(id),
            novoHorario,
            novaSala,
            novaCapacidade
        );

            Sessao sessaoAtualizada = sessaoRepositorio.obterPorId(new SessaoId(id));
            return ResponseEntity.ok(mapearSessaoParaDTO(sessaoAtualizada));
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

        RemarcarIngressosSessaoUseCase.RemarcacaoResultado resultado =
            remarcarIngressosSessaoUseCase.executar(
                new SessaoId(id),
                request.getNovoHorario(),
                request.getAssentosAfetados()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Ingressos remarcados com sucesso");
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
            removerSessaoUseCase.executar(new SessaoId(id));
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
     * Calcula a ocupação percentual de uma sessão
     */
    private double calcularOcupacao(Sessao sessao) {
        if (sessao.getCapacidade() == 0) return 0.0;

        long assentosOcupados = sessao.getMapaAssentosDisponiveis().values().stream()
                .filter(disponivel -> !disponivel)
                .count();

        // Retorna fração entre 0.0 e 1.0 (ex: 0.08 para 8%)
        return (double) assentosOcupados / (double) sessao.getCapacidade();
    }

    /**
     * Mapeia Sessao para DTO (Map)
     */
    private Map<String, Object> mapearSessaoParaDTO(Sessao sessao) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", sessao.getSessaoId().getId());
        dto.put("filmeId", sessao.getFilmeId().getId());
        dto.put("horario", sessao.getHorario());
        dto.put("status", sessao.getStatus().name());
        dto.put("sala", sessao.getSala());
        dto.put("capacidade", sessao.getCapacidade());

        // Adiciona informações do filme (inclui campo filmeTitulo para compatibilidade)
        try {
            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
            if (filme != null) {
                Map<String, Object> filmeInfo = new HashMap<>();
                filmeInfo.put("id", filme.getFilmeId().getId());
                filmeInfo.put("titulo", filme.getTitulo());
                filmeInfo.put("duracao", filme.getDuracao());
                filmeInfo.put("classificacaoEtaria", filme.getClassificacaoEtaria());
                filmeInfo.put("imagemUrl", filme.getImagemUrl());
                dto.put("filme", filmeInfo);
                dto.put("filmeTitulo", filme.getTitulo());
            } else {
                dto.put("filmeTitulo", "Filme #" + sessao.getFilmeId().getId());
            }
        } catch (Exception e) {
            dto.put("filmeTitulo", "Filme #" + sessao.getFilmeId().getId());
        }

        // Adiciona estatísticas de ocupação
        long assentosDisponiveis = sessao.getMapaAssentosDisponiveis().values().stream()
                .filter(disponivel -> disponivel)
                .count();
        long assentosOcupados = sessao.getCapacidade() - assentosDisponiveis;

        dto.put("assentosDisponiveis", assentosDisponiveis);
        dto.put("assentosOcupados", assentosOcupados);
        dto.put("assentosReservados", assentosOcupados);  // Alias para compatibilidade
        dto.put("ocupacao", Math.round(calcularOcupacao(sessao) * 100.0) / 100.0);

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
        private String estrategia; // "MASSA" ou "INDIVIDUAL"
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
