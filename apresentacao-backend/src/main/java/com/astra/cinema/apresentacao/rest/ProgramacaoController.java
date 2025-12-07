package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.dominio.programacao.ProgramacaoService;
import com.astra.cinema.dominio.comum.ProgramacaoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.programacao.Programacao;
import com.astra.cinema.dominio.programacao.ProgramacaoRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/programacoes")
@CrossOrigin(origins = "*")
public class ProgramacaoController {

    private final ProgramacaoRepositorio programacaoRepositorio;
    private final ProgramacaoService programacaoService;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public ProgramacaoController(
            ProgramacaoRepositorio programacaoRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        this.programacaoRepositorio = programacaoRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.programacaoService = new ProgramacaoService(programacaoRepositorio, sessaoRepositorio);
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Programacao> programacoes = programacaoRepositorio.listarProgramacoes();

            List<Map<String, Object>> response = programacoes.stream()
                    .map(this::mapearProgramacaoComDetalhes)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao listar programações: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalhar(@PathVariable Integer id) {
        try {
            Programacao programacao = programacaoRepositorio.obterPorId(new ProgramacaoId(id));
            if (programacao == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(mapearProgramacaoComDetalhes(programacao));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao buscar programação: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarProgramacaoRequest request) {
        try {
            // Valida autorizacao
            String cargoStr = request.funcionario != null ? request.funcionario.cargo : "GERENTE";
            String nome = request.funcionario != null ? request.funcionario.nome : "Admin";
            Funcionario funcionario = new Funcionario(nome, Cargo.valueOf(cargoStr.toUpperCase()));

            // Parse das datas
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date periodoInicio = sdf.parse(request.periodoInicio);
            Date periodoFim = sdf.parse(request.periodoFim);

            // Converte IDs de sessão
            List<SessaoId> sessaoIds = request.sessaoIds.stream()
                    .map(SessaoId::new)
                    .collect(Collectors.toList());

            // Cria programação usando o service (valida RN11 e RN12)
            Programacao programacao = programacaoService.criarProgramacao(
                    funcionario,
                    periodoInicio,
                    periodoFim,
                    sessaoIds
            );

            return ResponseEntity.ok(mapearProgramacaoComDetalhes(programacao));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao criar programação: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Integer id) {
        try {
            ProgramacaoId programacaoId = new ProgramacaoId(id);
            programacaoService.removerProgramacao(programacaoId);
            return ResponseEntity.ok(Map.of("mensagem", "Programação removida com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao remover programação: " + e.getMessage()));
        }
    }

    private Map<String, Object> mapearProgramacaoComDetalhes(Programacao programacao) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", programacao.getProgramacaoId().getId());
        map.put("periodoInicio", programacao.getPeriodoInicio());
        map.put("periodoFim", programacao.getPeriodoFim());

        // Mapear sessões com detalhes
        List<Map<String, Object>> sessoes = programacao.getSessoes().stream()
                .map(sessaoId -> {
                    Map<String, Object> sessaoMap = new HashMap<>();
                    sessaoMap.put("id", sessaoId.getId());

                    try {
                        var sessao = sessaoRepositorio.obterPorId(sessaoId);
                        if (sessao != null) {
                            sessaoMap.put("horario", sessao.getHorario());
                            sessaoMap.put("salaId", sessao.getSalaId().getId());
                            sessaoMap.put("sala", "Sala " + sessao.getSalaId().getId());
                            sessaoMap.put("status", sessao.getStatus().name());

                            var filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
                            if (filme != null) {
                                sessaoMap.put("filmeTitulo", filme.getTitulo());
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    return sessaoMap;
                })
                .collect(Collectors.toList());

        map.put("sessoes", sessoes);
        map.put("quantidadeSessoes", sessoes.size());

        return map;
    }

    public record CriarProgramacaoRequest(
            String periodoInicio,
            String periodoFim,
            List<Integer> sessaoIds,
            FuncionarioInfo funcionario
    ) {}

    public record FuncionarioInfo(String nome, String cargo) {}
}
