package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.funcionario.ConsultarHistoricoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.RemarcarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.ValidarIngressoFuncionarioUseCase;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.sessao.Sessao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de funcionários.
 * Endpoints:
 * - POST /api/funcionario/ingressos/validar - Validar ingresso
 * - GET /api/funcionario/ingressos/historico - Listar histórico de validações
 * - GET /api/funcionario/ingressos/ativos - Listar ingressos ativos para remarcação
 * - POST /api/funcionario/ingressos/remarcar - Remarcar ingresso
 */
@RestController
@RequestMapping("/api/funcionario")
@CrossOrigin(origins = "*")
public class FuncionarioOperacoesController {

    private final ValidarIngressoFuncionarioUseCase validarIngressoUseCase;
    private final ConsultarHistoricoFuncionarioUseCase consultarHistoricoUseCase;
    private final RemarcarIngressoFuncionarioUseCase remarcarIngressoUseCase;
    private final CompraRepositorio compraRepositorio;

    public FuncionarioOperacoesController(
            ValidarIngressoFuncionarioUseCase validarIngressoUseCase,
            ConsultarHistoricoFuncionarioUseCase consultarHistoricoUseCase,
            RemarcarIngressoFuncionarioUseCase remarcarIngressoUseCase,
            CompraRepositorio compraRepositorio) {
        this.validarIngressoUseCase = validarIngressoUseCase;
        this.consultarHistoricoUseCase = consultarHistoricoUseCase;
        this.remarcarIngressoUseCase = remarcarIngressoUseCase;
        this.compraRepositorio = compraRepositorio;
    }

    /**
     * Valida um ingresso pelo QR Code.
     */
    @PostMapping("/ingressos/validar")
    public ResponseEntity<?> validarIngresso(@RequestBody ValidarIngressoRequest request) {
        try {
            // Por enquanto, usa um funcionário padrão (ID 1)
            // TODO: Pegar do contexto de autenticação
            FuncionarioId funcionarioId = new FuncionarioId(1);

            ValidarIngressoFuncionarioUseCase.ResultadoValidacaoFuncionario resultado =
                validarIngressoUseCase.executar(request.qrCode, funcionarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("valido", resultado.isValido());
            response.put("mensagem", resultado.getMensagem());

            if (resultado.getIngresso() != null) {
                response.put("ingresso", mapearIngressoComTodosAssentos(resultado.getIngresso(), request.qrCode));
            }

            if (resultado.getSessao() != null) {
                response.put("sessao", mapearSessao(resultado.getSessao()));
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("valido", false);
            erro.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("valido", false);
            erro.put("erro", "Erro interno ao validar ingresso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * Lista o histórico de validações.
     */
    @GetMapping("/ingressos/historico")
    public ResponseEntity<?> listarHistorico() {
        try {
            List<ConsultarHistoricoFuncionarioUseCase.ItemHistorico> historico =
                consultarHistoricoUseCase.listarTodasValidacoes();

            List<Map<String, Object>> response = historico.stream()
                .map(this::mapearItemHistorico)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao carregar histórico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * Lista ingressos ativos (válidos) para possível remarcação.
     */
    @GetMapping("/ingressos/ativos")
    public ResponseEntity<?> listarIngressosAtivos() {
        try {
            List<ConsultarHistoricoFuncionarioUseCase.IngressoAtivo> ingressos =
                consultarHistoricoUseCase.listarIngressosAtivos();

            List<Map<String, Object>> response = ingressos.stream()
                .map(this::mapearIngressoAtivo)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao carregar ingressos ativos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * Remarca um ingresso para outra sessão.
     */
    @PostMapping("/ingressos/remarcar")
    public ResponseEntity<?> remarcarIngresso(@RequestBody RemarcarIngressoRequest request) {
        try {
            // Por enquanto, usa um funcionário padrão (ID 1)
            // TODO: Pegar do contexto de autenticação
            FuncionarioId funcionarioId = new FuncionarioId(1);

            IngressoId ingressoId = new IngressoId(request.ingressoId);
            SessaoId novaSessaoId = new SessaoId(request.novaSessaoId);
            AssentoId novoAssentoId = request.novoAssentoId != null ?
                new AssentoId(request.novoAssentoId) : null;

            RemarcarIngressoFuncionarioUseCase.ResultadoRemarcacao resultado =
                remarcarIngressoUseCase.executar(
                    ingressoId,
                    novaSessaoId,
                    novoAssentoId,
                    funcionarioId,
                    request.motivoTecnico
                );

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", resultado.isSucesso());
            response.put("mensagem", resultado.getMensagem());
            response.put("ingresso", mapearIngresso(resultado.getIngresso()));
            response.put("sessaoOriginal", mapearSessao(resultado.getSessaoOriginal()));
            response.put("novaSessao", mapearSessao(resultado.getNovaSessao()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Erro interno ao remarcar ingresso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    // Métodos auxiliares de mapeamento

    private Map<String, Object> mapearIngressoComTodosAssentos(Ingresso ingresso, String qrCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ingresso.getIngressoId().getId());
        map.put("qrCode", ingresso.getQrCode());
        map.put("tipo", ingresso.getTipo().toString());
        map.put("status", ingresso.getStatus().toString());
        map.put("sessaoId", ingresso.getSessaoId().getId());

        // Buscar todos os assentos da compra
        try {
            Compra compra = compraRepositorio.buscarCompraPorQrCode(qrCode);
            if (compra != null && compra.getIngressos() != null) {
                String todosAssentos = compra.getIngressos().stream()
                    .map(i -> i.getAssentoId().getValor())
                    .collect(Collectors.joining(", "));
                map.put("assento", todosAssentos);
            } else {
                map.put("assento", ingresso.getAssentoId().getValor());
            }
        } catch (Exception e) {
            // Se falhar ao buscar compra, usa apenas o assento do ingresso
            map.put("assento", ingresso.getAssentoId().getValor());
        }

        return map;
    }

    private Map<String, Object> mapearIngresso(Ingresso ingresso) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ingresso.getIngressoId().getId());
        map.put("qrCode", ingresso.getQrCode());
        map.put("assento", ingresso.getAssentoId().getValor());
        map.put("tipo", ingresso.getTipo().toString());
        map.put("status", ingresso.getStatus().toString());
        map.put("sessaoId", ingresso.getSessaoId().getId());
        return map;
    }

    private Map<String, Object> mapearSessao(Sessao sessao) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", sessao.getSessaoId() != null ? sessao.getSessaoId().getId() : null);
        map.put("sala", sessao.getSala());
        map.put("horario", sessao.getHorario());
        map.put("filmeId", sessao.getFilmeId() != null ? sessao.getFilmeId().getId() : null);
        return map;
    }

    private Map<String, Object> mapearItemHistorico(ConsultarHistoricoFuncionarioUseCase.ItemHistorico item) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getValidacaoId());
        map.put("ingressoId", item.getIngressoId());
        map.put("qrCode", item.getQrCode());
        map.put("assento", item.getAssento());
        map.put("status", item.getStatus());
        map.put("sessaoId", item.getSessaoId());
        map.put("sucesso", item.isSucesso());
        map.put("mensagem", item.getMensagem());
        map.put("dataHora", item.getDataHora());
        return map;
    }

    private Map<String, Object> mapearIngressoAtivo(ConsultarHistoricoFuncionarioUseCase.IngressoAtivo ingresso) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ingresso.getId());
        map.put("qrCode", ingresso.getQrCode());
        map.put("tipo", ingresso.getTipo());
        map.put("status", ingresso.getStatus());
        map.put("assento", ingresso.getAssento());
        map.put("sessaoId", ingresso.getSessaoId());
        map.put("sala", ingresso.getSala());
        map.put("horario", ingresso.getHorario());
        return map;
    }

    // DTOs para requests

    public record ValidarIngressoRequest(String qrCode) {}

    public record RemarcarIngressoRequest(
        Integer ingressoId,
        Integer novaSessaoId,
        String novoAssentoId,
        String motivoTecnico
    ) {}
}
