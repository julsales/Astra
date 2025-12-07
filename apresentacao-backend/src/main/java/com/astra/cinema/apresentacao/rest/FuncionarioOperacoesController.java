package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.funcionario.ConsultarHistoricoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.RemarcarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.ValidarIngressoFuncionarioUseCase;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpaRepository;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpa;
import com.astra.cinema.infraestrutura.persistencia.jpa.ProdutoJpaRepository;
import com.astra.cinema.infraestrutura.persistencia.jpa.ProdutoJpa;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final com.astra.cinema.dominio.filme.FilmeRepositorio filmeRepositorio;
    private final VendaJpaRepository vendaJpaRepository;
    private final ProdutoJpaRepository produtoJpaRepository;

    public FuncionarioOperacoesController(
            ValidarIngressoFuncionarioUseCase validarIngressoUseCase,
            ConsultarHistoricoFuncionarioUseCase consultarHistoricoUseCase,
            RemarcarIngressoFuncionarioUseCase remarcarIngressoUseCase,
            CompraRepositorio compraRepositorio,
            com.astra.cinema.dominio.filme.FilmeRepositorio filmeRepositorio,
            VendaJpaRepository vendaJpaRepository,
            ProdutoJpaRepository produtoJpaRepository) {
        this.validarIngressoUseCase = validarIngressoUseCase;
        this.consultarHistoricoUseCase = consultarHistoricoUseCase;
        this.remarcarIngressoUseCase = remarcarIngressoUseCase;
        this.compraRepositorio = compraRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.vendaJpaRepository = vendaJpaRepository;
        this.produtoJpaRepository = produtoJpaRepository;
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
     * Lista sessões agrupadas por filme para remarcação inteligente.
     * Retorna apenas sessões com status DISPONIVEL ou PROBLEMA_TECNICO.
     */
    @GetMapping("/sessoes/para-remarcacao")
    public ResponseEntity<?> listarSessoesParaRemarcacao() {
        try {
            List<ConsultarHistoricoFuncionarioUseCase.IngressoAtivo> ingressos =
                consultarHistoricoUseCase.listarIngressosAtivos();

            // Agrupar ingressos por sessão e filme
            Map<String, Object> response = new HashMap<>();

            // Criar estrutura: filme -> sessões -> ingressos
            Map<Integer, Map<String, Object>> filmes = new HashMap<>();

            for (ConsultarHistoricoFuncionarioUseCase.IngressoAtivo ingresso : ingressos) {
                Integer filmeId = ingresso.getFilmeId();
                Integer sessaoId = ingresso.getSessaoId();

                // Inicializar filme se não existir
                if (!filmes.containsKey(filmeId)) {
                    Map<String, Object> filme = new HashMap<>();
                    filme.put("filmeId", filmeId);
                    filme.put("filmeTitulo", ingresso.getFilmeTitulo());
                    filme.put("sessoes", new HashMap<Integer, Map<String, Object>>());
                    filmes.put(filmeId, filme);
                }

                Map<String, Object> filme = filmes.get(filmeId);
                @SuppressWarnings("unchecked")
                Map<Integer, Map<String, Object>> sessoes =
                    (Map<Integer, Map<String, Object>>) filme.get("sessoes");

                // Inicializar sessão se não existir
                if (!sessoes.containsKey(sessaoId)) {
                    Map<String, Object> sessao = new HashMap<>();
                    sessao.put("sessaoId", sessaoId);
                    sessao.put("sala", ingresso.getSala()); // Já retorna String do Use Case
                    sessao.put("horario", ingresso.getHorario());
                    sessao.put("statusSessao", ingresso.getStatusSessao());
                    sessao.put("ingressos", new java.util.ArrayList<Map<String, Object>>());
                    sessao.put("totalIngressos", 0);
                    sessoes.put(sessaoId, sessao);
                }

                Map<String, Object> sessao = sessoes.get(sessaoId);
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> ingressosList =
                    (List<Map<String, Object>>) sessao.get("ingressos");

                ingressosList.add(mapearIngressoAtivo(ingresso));
                sessao.put("totalIngressos", ingressosList.size());
            }

            // Converter para lista e buscar títulos dos filmes
            List<Map<String, Object>> filmesLista = new java.util.ArrayList<>();
            for (Map<String, Object> filme : filmes.values()) {
                // Buscar título do filme diretamente
                Integer filmeId = (Integer) filme.get("filmeId");
                try {
                    com.astra.cinema.dominio.filme.Filme filmeEntidade =
                        filmeRepositorio.obterPorId(new com.astra.cinema.dominio.comum.FilmeId(filmeId));
                    if (filmeEntidade != null && filmeEntidade.getTitulo() != null) {
                        filme.put("filmeTitulo", filmeEntidade.getTitulo());
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao buscar título do filme " + filmeId + ": " + e.getMessage());
                }

                @SuppressWarnings("unchecked")
                Map<Integer, Map<String, Object>> sessoesMap =
                    (Map<Integer, Map<String, Object>>) filme.get("sessoes");
                filme.put("sessoes", new java.util.ArrayList<>(sessoesMap.values()));
                filmesLista.add(filme);
            }

            response.put("filmes", filmesLista);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao carregar sessões para remarcação: " + e.getMessage());
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
        map.put("salaId", sessao.getSalaId() != null ? sessao.getSalaId().getId() : null);
        map.put("sala", "Sala " + (sessao.getSalaId() != null ? sessao.getSalaId().getId() : ""));
        map.put("horario", sessao.getHorario());
        map.put("filmeId", sessao.getFilmeId() != null ? sessao.getFilmeId().getId() : null);
        return map;
    }

    private Map<String, Object> mapearItemHistorico(ConsultarHistoricoFuncionarioUseCase.ItemHistorico item) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getValidacaoId());
        map.put("ingressoId", item.getIngressoId());
        map.put("compraId", item.getCompraId());
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

    /**
     * Lista compras ativas (com ingressos válidos) para remarcação.
     */
    @GetMapping("/compras/ativas")
    public ResponseEntity<?> listarComprasAtivas() {
        try {
            List<Compra> compras = compraRepositorio.listarTodas();

            // Mostrar compras que possuam ingressos com status ATIVO
            List<Map<String, Object>> response = compras.stream()
                .filter(c -> c.getIngressos() != null && !c.getIngressos().isEmpty())
                .filter(c -> c.getIngressos().stream().anyMatch(i ->
                    i.getStatus() == com.astra.cinema.dominio.compra.StatusIngresso.ATIVO))
                .map(this::mapearCompraComIngressos)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao carregar compras ativas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * Busca compras por termo (QR Code, ID ou nome do cliente).
     */
    @GetMapping("/compras/buscar")
    public ResponseEntity<?> buscarCompras(@RequestParam String termo) {
        try {
            List<Compra> todasCompras = compraRepositorio.listarTodas();
            String termoBusca = termo.toLowerCase().trim();

            List<Map<String, Object>> response = todasCompras.stream()
                .filter(c -> c.getStatus() == com.astra.cinema.dominio.compra.StatusCompra.CONFIRMADA)
                .filter(c -> {
                    // Busca por ID da compra
                    if (c.getCompraId() != null &&
                        String.valueOf(c.getCompraId().getId()).contains(termoBusca)) {
                        return true;
                    }
                    // Busca por QR Code de qualquer ingresso
                    if (c.getIngressos() != null) {
                        return c.getIngressos().stream().anyMatch(i ->
                            i.getQrCode() != null &&
                            i.getQrCode().toLowerCase().contains(termoBusca));
                    }
                    return false;
                })
                .map(this::mapearCompraComIngressos)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao buscar compras: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * Remarca múltiplos ingressos de uma só vez.
     */
    @PostMapping("/ingressos/remarcar-multiplos")
    public ResponseEntity<?> remarcarMultiplosIngressos(@RequestBody RemarcarMultiplosRequest request) {
        try {
            FuncionarioId funcionarioId = new FuncionarioId(1);
            List<Map<String, Object>> resultados = new java.util.ArrayList<>();
            int sucessos = 0;
            int falhas = 0;

            for (RemarcarIngressoRequest remarcacao : request.remarcacoes) {
                try {
                    IngressoId ingressoId = new IngressoId(remarcacao.ingressoId);
                    SessaoId novaSessaoId = new SessaoId(remarcacao.novaSessaoId);
                    AssentoId novoAssentoId = remarcacao.novoAssentoId != null ?
                        new AssentoId(remarcacao.novoAssentoId) : null;

                    RemarcarIngressoFuncionarioUseCase.ResultadoRemarcacao resultado =
                        remarcarIngressoUseCase.executar(
                            ingressoId,
                            novaSessaoId,
                            novoAssentoId,
                            funcionarioId,
                            remarcacao.motivoTecnico
                        );

                    Map<String, Object> itemResultado = new HashMap<>();
                    itemResultado.put("ingressoId", remarcacao.ingressoId);
                    itemResultado.put("sucesso", resultado.isSucesso());
                    itemResultado.put("mensagem", resultado.getMensagem());
                    resultados.add(itemResultado);

                    if (resultado.isSucesso()) {
                        sucessos++;
                    } else {
                        falhas++;
                    }
                } catch (Exception e) {
                    Map<String, Object> itemResultado = new HashMap<>();
                    itemResultado.put("ingressoId", remarcacao.ingressoId);
                    itemResultado.put("sucesso", false);
                    itemResultado.put("mensagem", e.getMessage());
                    resultados.add(itemResultado);
                    falhas++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sucessos", sucessos);
            response.put("falhas", falhas);
            response.put("resultados", resultados);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao remarcar ingressos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    private Map<String, Object> mapearCompraComIngressos(Compra compra) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", compra.getCompraId() != null ? compra.getCompraId().getId() : null);
        map.put("status", compra.getStatus().toString());
        map.put("clienteNome", "Cliente #" + (compra.getClienteId() != null ? compra.getClienteId().getId() : "?"));

        if (compra.getIngressos() != null) {
            List<Map<String, Object>> ingressos = compra.getIngressos().stream()
                .filter(i -> i.getStatus() == com.astra.cinema.dominio.compra.StatusIngresso.ATIVO)
                .map(this::mapearIngresso)
                .collect(Collectors.toList());
            map.put("ingressos", ingressos);
        } else {
            map.put("ingressos", List.of());
        }

        return map;
    }

    /**
     * Retorna estatísticas do funcionário para o dashboard.
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<?> obterEstatisticas() {
        try {
            // Contadores reais
            List<ConsultarHistoricoFuncionarioUseCase.ItemHistorico> historico =
                consultarHistoricoUseCase.listarTodasValidacoes();

            List<ConsultarHistoricoFuncionarioUseCase.IngressoAtivo> ingressosAtivos =
                consultarHistoricoUseCase.listarIngressosAtivos();

            // Calcular estatísticas
            long totalValidacoes = historico.size();
            long validacoesHoje = historico.stream()
                .filter(h -> {
                    if (h.getDataHora() == null) return false;
                    java.time.LocalDate dataValidacao = h.getDataHora().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                    return dataValidacao.equals(java.time.LocalDate.now());
                })
                .count();

            long validacoesSucesso = historico.stream()
                .filter(ConsultarHistoricoFuncionarioUseCase.ItemHistorico::isSucesso)
                .count();

            long ingressosPendentes = ingressosAtivos.stream()
                .filter(i -> "ATIVO".equals(i.getStatus()) || "PENDENTE".equals(i.getStatus()))
                .count();

            // Calcular vendas (bomboniere + ingressos)
            LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime fimDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            // Vendas da bomboniere (hoje)
            List<VendaJpa> vendasBomboniereHoje = vendaJpaRepository.findAll().stream()
                .filter(v -> v.getCriadoEm() != null &&
                        v.getCriadoEm().isAfter(inicioDia) &&
                        v.getCriadoEm().isBefore(fimDia))
                .toList();

            double totalVendasBomboniereHoje = vendasBomboniereHoje.stream()
                .mapToDouble(v -> {
                    ProdutoJpa produto = produtoJpaRepository.findById(v.getProdutoId()).orElse(null);
                    return produto != null ? produto.getPreco() * v.getQuantidade() : 0.0;
                })
                .sum();

            // Vendas de ingressos (total de todas as compras confirmadas)
            List<Compra> todasCompras = compraRepositorio.listarTodas();
            double totalVendasIngressos = todasCompras.stream()
                .filter(c -> c.getStatus() == com.astra.cinema.dominio.compra.StatusCompra.CONFIRMADA)
                .filter(c -> c.getPagamentoId() != null)
                .mapToDouble(c -> {
                    // Aproximadamente R$ 25 por ingresso (valor médio)
                    return c.getIngressos() != null ? c.getIngressos().size() * 25.0 : 0.0;
                })
                .sum();

            // Total geral de vendas
            double totalVendas = totalVendasBomboniereHoje + totalVendasIngressos;

            Map<String, Object> response = new HashMap<>();
            response.put("totalValidacoes", totalValidacoes);
            response.put("validacoesHoje", validacoesHoje);
            response.put("validacoesSucesso", validacoesSucesso);
            response.put("ingressosPendentes", ingressosPendentes);
            response.put("ingressosAtivos", ingressosAtivos.size());

            // Dados de vendas
            response.put("vendasHoje", vendasBomboniereHoje.size());
            response.put("totalVendas", Math.round(totalVendas * 100) / 100.0);
            response.put("totalVendasBomboniere", Math.round(totalVendasBomboniereHoje * 100) / 100.0);
            response.put("totalVendasIngressos", Math.round(totalVendasIngressos * 100) / 100.0);

            // Taxa de sucesso
            double taxaSucesso = totalValidacoes > 0
                ? (validacoesSucesso * 100.0 / totalValidacoes)
                : 0;
            response.put("taxaSucesso", Math.round(taxaSucesso * 10) / 10.0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao carregar estatísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    // DTOs para requests

    public record ValidarIngressoRequest(String qrCode) {}

    public record RemarcarIngressoRequest(
        Integer ingressoId,
        Integer novaSessaoId,
        String novoAssentoId,
        String motivoTecnico
    ) {}

    public record RemarcarMultiplosRequest(
        List<RemarcarIngressoRequest> remarcacoes
    ) {}
}
