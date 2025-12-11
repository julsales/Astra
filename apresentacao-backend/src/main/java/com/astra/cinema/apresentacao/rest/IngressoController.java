package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.ingresso.RemarcarIngressoUseCase;
import com.astra.cinema.aplicacao.ingresso.ValidarIngressoUseCase;
import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.bomboniere.Venda;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.operacao.RemarcacaoSessao;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingressos")
@CrossOrigin(origins = "*")
public class IngressoController {
    private static final Logger logger = LoggerFactory.getLogger(IngressoController.class);

    private final ValidarIngressoUseCase validarIngressoUseCase;
    private final RemarcarIngressoUseCase remarcarIngressoUseCase;
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final VendaRepositorio vendaRepositorio;
    private final RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio;

    public IngressoController(ValidarIngressoUseCase validarIngressoUseCase,
                             RemarcarIngressoUseCase remarcarIngressoUseCase,
                             CompraRepositorio compraRepositorio,
                             SessaoRepositorio sessaoRepositorio,
                             FilmeRepositorio filmeRepositorio,
                             VendaRepositorio vendaRepositorio,
                             RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio) {
        this.validarIngressoUseCase = validarIngressoUseCase;
        this.remarcarIngressoUseCase = remarcarIngressoUseCase;
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.vendaRepositorio = vendaRepositorio;
        this.remarcacaoSessaoRepositorio = remarcacaoSessaoRepositorio;
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
            logger.info("Recebida solicitação de remarcação. QRCode: {}, Nova Sessão: {}, Novo Assento: {}", 
                request.getQrCode(), request.getNovaSessaoId(), request.getNovoAssentoId());

            remarcarIngressoUseCase.executar(
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

    // Método auxiliar para buscar produtos de uma compra
    private List<Map<String, Object>> buscarProdutosDaCompra(com.astra.cinema.dominio.comum.CompraId compraId) {
        try {
            List<Venda> vendas = vendaRepositorio.buscarPorCompra(compraId);
            List<Map<String, Object>> produtos = new ArrayList<>();

            for (Venda venda : vendas) {
                // Contar produtos por nome (agrupar por produto)
                Map<String, Integer> contagemPorProduto = new HashMap<>();
                Map<String, Produto> produtoPorNome = new HashMap<>();

                for (Produto produto : venda.getProdutos()) {
                    String nome = produto.getNome();
                    contagemPorProduto.put(nome, contagemPorProduto.getOrDefault(nome, 0) + 1);
                    produtoPorNome.put(nome, produto);
                }

                // Adicionar produtos únicos com quantidade
                for (Map.Entry<String, Integer> entry : contagemPorProduto.entrySet()) {
                    String nome = entry.getKey();
                    Integer quantidade = entry.getValue();
                    Produto produto = produtoPorNome.get(nome);

                    Map<String, Object> produtoMap = new HashMap<>();
                    produtoMap.put("id", produto.getProdutoId().getId());
                    produtoMap.put("nome", produto.getNome());
                    produtoMap.put("preco", produto.getPreco());
                    produtoMap.put("quantidade", quantidade);
                    produtos.add(produtoMap);
                }
            }

            return produtos;
        } catch (Exception e) {
            return new ArrayList<>(); // Retorna lista vazia em caso de erro
        }
    }

    @GetMapping
    public ResponseEntity<?> buscarIngressos(@RequestParam(required = false) Integer clienteId) {
        try {
            logger.info("Buscando ingressos para cliente: {}", clienteId);
            List<Compra> compras;

            // Se clienteId foi fornecido, busca TODAS as compras deste cliente (incluindo canceladas)
            if (clienteId != null) {
                compras = compraRepositorio.buscarPorCliente(new com.astra.cinema.dominio.comum.ClienteId(clienteId));
            } else {
                // Se não fornecido, precisa buscar todas as compras
                // Por enquanto, retorna lista vazia (ou poderia buscar todas)
                compras = new ArrayList<>();
            }

            // PROCESSAR CADA COMPRA (agrupando por sessão para lidar com remarcações parciais)
            List<Map<String, Object>> response = compras.stream()
                .flatMap(compra -> {
                    List<Ingresso> todosIngressos = compra.getIngressos();
                    if (todosIngressos.isEmpty()) return java.util.stream.Stream.empty();

                    // Agrupar ingressos por SessãoId
                    Map<SessaoId, List<Ingresso>> ingressosPorSessao = todosIngressos.stream()
                        .collect(Collectors.groupingBy(Ingresso::getSessaoId));
                    
                    logger.info("Compra ID: {}. Grupos de sessão encontrados: {}", compra.getCompraId().getId(), ingressosPorSessao.keySet());

                    // Para cada grupo de sessão, cria um card separado
                    return ingressosPorSessao.values().stream()
                        .map(grupo -> converterParaDto(compra, grupo));
                })
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
            List<Compra> compras;

            // Se clienteId foi fornecido, busca compras deste cliente e filtra por ingressos ativos
            if (clienteId != null) {
                compras = compraRepositorio.buscarPorCliente(new com.astra.cinema.dominio.comum.ClienteId(clienteId));
            } else {
                // Se não fornecido, busca todas as compras
                compras = compraRepositorio.listarTodas();
            }

            // Filtrar compras que tenham pelo menos um ingresso ativo
            compras = compras.stream()
                .filter(compra -> compra.getIngressos().stream()
                    .anyMatch(ing -> ing.getStatus() == StatusIngresso.ATIVO))
                .collect(Collectors.toList());

            // PROCESSAR CADA COMPRA (agrupando por sessão para lidar com remarcações parciais)
            List<Map<String, Object>> response = compras.stream()
                .flatMap(compra -> {
                    // Pega apenas os ingressos ativos desta compra
                    List<Ingresso> grupo = compra.getIngressos().stream()
                        .filter(ing -> ing.getStatus() == StatusIngresso.ATIVO)
                        .collect(Collectors.toList());

                    if (grupo.isEmpty()) return java.util.stream.Stream.empty();

                    // Agrupar ingressos por SessãoId
                    Map<SessaoId, List<Ingresso>> ingressosPorSessao = grupo.stream()
                        .collect(Collectors.groupingBy(Ingresso::getSessaoId));
                    
                    // Para cada grupo de sessão, cria um card separado
                    return ingressosPorSessao.values().stream()
                        .map(g -> converterParaDto(compra, g));
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

    private Map<String, Object> converterParaDto(Compra compra, List<Ingresso> grupo) {
        Ingresso i = grupo.stream()
            .min(Comparator.comparing(ing -> ing.getIngressoId().getId()))
            .orElse(grupo.get(0));
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", i.getIngressoId().getId());
        map.put("qrCode", i.getQrCode());
        map.put("codigo", i.getQrCode());
        map.put("sessaoId", i.getSessaoId().getId());

        String todosAssentos = grupo.stream()
            .map(ing -> ing.getAssentoId().getValor())
            .collect(Collectors.joining(", "));
        map.put("assento", todosAssentos);
        map.put("assentos", grupo.stream()
            .map(ing -> ing.getAssentoId().getValor())
            .collect(Collectors.toList()));

        // Calcula total dos ingressos
        double valorIngressos = grupo.stream()
            .mapToDouble(ing -> ing.getTipo() == com.astra.cinema.dominio.compra.TipoIngresso.INTEIRA ? 35.0 : 17.5)
            .sum();

        List<Map<String, String>> ingressosDetalhados = grupo.stream()
            .map(ing -> {
                Map<String, String> ingressoDetalhe = new HashMap<>();
                ingressoDetalhe.put("assento", ing.getAssentoId().getValor());
                ingressoDetalhe.put("tipo", ing.getTipo().name());
                return ingressoDetalhe;
            })
            .collect(Collectors.toList());
        map.put("ingressosDetalhados", ingressosDetalhados);

        map.put("assentoIndividual", i.getAssentoId().getValor());
        map.put("tipo", i.getTipo().name());
        map.put("status", i.getStatus().name());

        boolean foiValidado = i.getStatus() == StatusIngresso.VALIDADO;
        map.put("foiValidado", foiValidado);

        // Busca produtos e calcula total dos produtos
        double valorProdutos = 0.0;
        try {
            List<Map<String, Object>> produtosDaCompra = buscarProdutosDaCompra(compra.getCompraId());
            map.put("produtos", produtosDaCompra);

            // Soma o valor total dos produtos
            valorProdutos = produtosDaCompra.stream()
                .mapToDouble(p -> {
                    double preco = p.get("preco") != null ? (double) p.get("preco") : 0.0;
                    int quantidade = p.get("quantidade") != null ? (int) p.get("quantidade") : 0;
                    return preco * quantidade;
                })
                .sum();
        } catch (Exception e) {
            map.put("produtos", new ArrayList<>());
        }

        // Total = ingressos + produtos
        map.put("total", valorIngressos + valorProdutos);

        try {
            Sessao sessao = sessaoRepositorio.obterPorId(i.getSessaoId());
            if (sessao != null) {
                map.put("horario", sessao.getHorario().toString());
                map.put("salaId", sessao.getSalaId().getId());
                map.put("sala", "Sala " + sessao.getSalaId().getId());

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
                } catch (Exception e) {}
            }
        } catch (Exception e) {}

        // Buscar histórico de remarcação
        try {
            List<RemarcacaoSessao> remarcacoes = remarcacaoSessaoRepositorio.listarPorIngresso(i.getIngressoId());
            if (!remarcacoes.isEmpty()) {
                map.put("remarcado", true);
                
                // Pega a remarcação mais recente
                RemarcacaoSessao remarcacaoRecente = remarcacoes.stream()
                    .max(Comparator.comparing(RemarcacaoSessao::getDataHoraRemarcacao))
                    .orElse(remarcacoes.get(0));
                
                Map<String, Object> historicoMap = new HashMap<>();
                historicoMap.put("dataRemarcacao", remarcacaoRecente.getDataHoraRemarcacao().toString());
                historicoMap.put("motivo", remarcacaoRecente.getMotivoTecnico());
                
                if (remarcacaoRecente.getAssentoOriginal() != null) {
                    historicoMap.put("assentoOriginal", remarcacaoRecente.getAssentoOriginal().getValor());
                }
                
                // Buscar sessão original
                try {
                    Sessao sessaoOriginal = sessaoRepositorio.obterPorId(remarcacaoRecente.getSessaoOriginal());
                    if (sessaoOriginal != null) {
                        Map<String, Object> sessaoOriginalMap = new HashMap<>();
                        sessaoOriginalMap.put("horario", sessaoOriginal.getHorario().toString());
                        sessaoOriginalMap.put("sala", "Sala " + sessaoOriginal.getSalaId().getId());
                        
                        try {
                            Filme filmeOriginal = filmeRepositorio.obterPorId(sessaoOriginal.getFilmeId());
                            if (filmeOriginal != null) {
                                sessaoOriginalMap.put("filme", filmeOriginal.getTitulo());
                            }
                        } catch (Exception e) {}
                        
                        historicoMap.put("sessaoOriginal", sessaoOriginalMap);
                    }
                } catch (Exception e) {}
                
                map.put("historicoRemarcacao", historicoMap);
            } else {
                map.put("remarcado", false);
            }
        } catch (Exception e) {
            map.put("remarcado", false);
        }

        return map;
    }
}
