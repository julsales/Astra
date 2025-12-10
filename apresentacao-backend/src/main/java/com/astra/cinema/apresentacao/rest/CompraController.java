package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.compra.CancelarCompraUseCase;
import com.astra.cinema.aplicacao.compra.IniciarCompraUseCase;
import com.astra.cinema.apresentacao.dto.mapper.IngressoMapper;
import com.astra.cinema.apresentacao.dto.request.CriarCompraRequest;
import com.astra.cinema.apresentacao.dto.response.IngressoDTO;
import com.astra.cinema.apresentacao.exception.RecursoNaoEncontradoException;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpa;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpaRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Compra
 * Endpoints para criar, cancelar e consultar compras de ingressos
 */
@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    private static final Logger log = LoggerFactory.getLogger(CompraController.class);

    private final IniciarCompraUseCase iniciarCompraUseCase;
    private final CancelarCompraUseCase cancelarCompraUseCase;
    private final CompraRepositorio compraRepositorio;
    private final IngressoMapper ingressoMapper;
    private final ProdutoRepositorio produtoRepositorio;
    private final VendaJpaRepository vendaJpaRepository;

    public CompraController(IniciarCompraUseCase iniciarCompraUseCase,
                           CancelarCompraUseCase cancelarCompraUseCase,
                           CompraRepositorio compraRepositorio,
                           IngressoMapper ingressoMapper,
                           ProdutoRepositorio produtoRepositorio,
                           VendaJpaRepository vendaJpaRepository) {
        this.iniciarCompraUseCase = iniciarCompraUseCase;
        this.cancelarCompraUseCase = cancelarCompraUseCase;
        this.compraRepositorio = compraRepositorio;
        this.ingressoMapper = ingressoMapper;
        this.produtoRepositorio = produtoRepositorio;
        this.vendaJpaRepository = vendaJpaRepository;
    }

    /**
     * Cria uma nova compra de ingressos
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> criarCompra(@Valid @RequestBody CriarCompraRequest request) {
        log.info("Criando compra - clienteId={} sessaoId={} assentos={} tipoIngresso={}",
                request.getClienteId(), request.getSessaoId(), request.getAssentos(), request.getTipoIngresso());

        // Cria ingressos com ID temporário único (será substituído pelo banco)
        AtomicInteger idTemporario = new AtomicInteger(1);
        List<Ingresso> ingressos = request.getAssentos().stream()
            .map(assento -> {
                TipoIngresso tipo = request.getTipoIngresso() != null
                    ? TipoIngresso.valueOf(request.getTipoIngresso().toUpperCase())
                    : TipoIngresso.INTEIRA;

                int idTemp = idTemporario.getAndIncrement();
                return new Ingresso(
                    new IngressoId(idTemp),
                    new SessaoId(request.getSessaoId().intValue()),
                    new AssentoId(assento),
                    tipo,
                    StatusIngresso.ATIVO,
                    null // QR Code será gerado automaticamente
                );
            })
            .collect(Collectors.toList());

        log.info("Ingressos construídos: {}", ingressos.size());

        // Salva a compra PRIMEIRO (gera QR Codes automaticamente)
        Compra compra = iniciarCompraUseCase.executar(
            new ClienteId(request.getClienteId().intValue()),
            ingressos
        );

        // Processar produtos da bomboniere (se houver) e associar à compra
        if (request.getProdutos() != null && !request.getProdutos().isEmpty()) {
            log.info("Processando {} produtos da bomboniere para compra {}", request.getProdutos().size(), compra.getCompraId().getId());
            for (CriarCompraRequest.ItemProduto itemProduto : request.getProdutos()) {
                Produto produto = produtoRepositorio.obterPorId(new ProdutoId(itemProduto.getProdutoId()));

                if (produto == null) {
                    throw new RecursoNaoEncontradoException("Produto", Long.valueOf(itemProduto.getProdutoId()));
                }

                // Reduzir estoque
                log.info("Reduzindo estoque do produto {} em {} unidades", produto.getNome(), itemProduto.getQuantidade());
                produto.reduzirEstoque(itemProduto.getQuantidade());
                produtoRepositorio.salvar(produto);
                log.info("Estoque atualizado: {} agora tem {} unidades (compra {})", produto.getNome(), produto.getEstoque(), compra.getCompraId().getId());

                // Criar e salvar venda para cada produto (associada à compra)
                // Para cada unidade do produto, cria uma linha na tabela venda
                for (int i = 0; i < itemProduto.getQuantidade(); i++) {
                    VendaJpa vendaJpa = new VendaJpa();
                    vendaJpa.setProdutoId(produto.getProdutoId().getId());
                    vendaJpa.setQuantidade(1);
                    vendaJpa.setPagamentoId(null); // Opcional, compra já tem pagamento
                    vendaJpa.setStatus("CONFIRMADA");
                    vendaJpa.setCompraId(compra.getCompraId().getId()); // Associa à compra
                    vendaJpa.setCriadoEm(java.time.LocalDateTime.now());
                    vendaJpaRepository.save(vendaJpa);
                    log.info("Venda criada: produto {} (ID {}) associado à compra {}",
                        produto.getNome(), vendaJpa.getId(), compra.getCompraId().getId());
                }
            }
        }

        // Busca a compra salva para obter os QR Codes gerados
        Compra compraCompleta = compraRepositorio.obterPorId(compra.getCompraId());
        if (compraCompleta == null) {
            throw new RecursoNaoEncontradoException("Compra", Long.valueOf(compra.getCompraId().getId()));
        }

        // Mapeia ingressos com QR Codes e produtos da bomboniere
        List<IngressoDTO> ingressosDTO = compraCompleta.getIngressos().stream()
            .map(ingresso -> ingressoMapper.toDTOComProdutos(ingresso, compraCompleta.getCompraId().getId()))
            .collect(Collectors.toList());

        // Monta resposta com compatibilidade de campos
        Map<String, Object> response = Map.of(
            "compraId", compraCompleta.getCompraId().getId(),
            "clienteId", compraCompleta.getClienteId().getId(),
            "status", compraCompleta.getStatus().name(),
            "ingressos", ingressosDTO
        );

        log.info("Compra criada com sucesso - ID: {}", compraCompleta.getCompraId().getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela uma compra existente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelarCompra(@PathVariable Integer id) {
        log.info("Cancelando compra ID: {}", id);

        try {
            cancelarCompraUseCase.executar(new CompraId(id));
            log.info("Compra {} cancelada com sucesso", id);
            return ResponseEntity.ok(Map.of(
                "mensagem", "Compra cancelada com sucesso",
                "compraId", id
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao cancelar compra {}: {}", id, e.getMessage());
            return ResponseEntity.status(404).body(Map.of(
                "erro", e.getMessage(),
                "compraId", id
            ));
        }
    }

    /**
     * Obtém uma compra por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obterCompra(@PathVariable Integer id) {
        log.info("Buscando compra ID: {}", id);

        Compra compra = compraRepositorio.obterPorId(new CompraId(id));
        if (compra == null) {
            throw new RecursoNaoEncontradoException("Compra", Long.valueOf(id));
        }

        // Mapeia ingressos com produtos da bomboniere
        List<IngressoDTO> ingressosDTO = compra.getIngressos().stream()
            .map(ingresso -> ingressoMapper.toDTOComProdutos(ingresso, compra.getCompraId().getId()))
            .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
            "id", compra.getCompraId().getId(),
            "clienteId", compra.getClienteId().getId(),
            "status", compra.getStatus().name(),
            "ingressos", ingressosDTO
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtém detalhes de um ingresso específico por QR Code
     * Inclui informações sobre produtos da bomboniere comprados
     */
    @GetMapping("/ingresso/{qrCode}")
    public ResponseEntity<?> obterIngressoPorQrCode(@PathVariable String qrCode) {
        log.info("Buscando ingresso por QR Code: {}", qrCode);

        try {
            // Buscar ingresso pelo QR Code
            Ingresso ingresso = compraRepositorio.buscarIngressoPorQrCode(qrCode);
            
            if (ingresso == null) {
                throw new RecursoNaoEncontradoException("Ingresso com QR Code " + qrCode + " não encontrado");
            }
            
            // Buscar compra associada ao ingresso
            CompraId compraId = compraRepositorio.obterCompraIdPorIngresso(ingresso.getIngressoId());
            Compra compra = compraRepositorio.obterPorId(compraId);
            
            if (compra == null) {
                throw new RecursoNaoEncontradoException("Compra do ingresso não encontrada");
            }
            
            // Mapeia ingresso com produtos da bomboniere
            IngressoDTO ingressoDTO = ingressoMapper.toDTOComProdutos(ingresso, compra.getCompraId().getId());
            
            Map<String, Object> response = Map.of(
                "ingresso", ingressoDTO,
                "compraId", compra.getCompraId().getId(),
                "clienteId", compra.getClienteId().getId()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (RecursoNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar ingresso por QR Code: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Erro interno ao buscar ingresso: " + e.getMessage()
            ));
        }
    }
}

