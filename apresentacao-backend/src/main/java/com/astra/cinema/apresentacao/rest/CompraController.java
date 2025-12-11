package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.servicos.CompraAppService;
import com.astra.cinema.apresentacao.dto.mapper.IngressoMapper;
import com.astra.cinema.apresentacao.dto.request.CriarCompraRequest;
import com.astra.cinema.apresentacao.dto.response.IngressoDTO;
import com.astra.cinema.apresentacao.exception.RecursoNaoEncontradoException;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
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
 * REFATORADO: Agora usa apenas CompraAppService e IngressoService (sem acesso direto a repositórios)
 */
@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    private static final Logger log = LoggerFactory.getLogger(CompraController.class);

    private final CompraAppService compraAppService;
    private final IngressoMapper ingressoMapper;

    public CompraController(
            CompraAppService compraAppService,
            IngressoMapper ingressoMapper) {
        this.compraAppService = compraAppService;
        this.ingressoMapper = ingressoMapper;
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

        // Converte produtos para o formato do serviço
        List<CompraAppService.ItemProduto> produtos = null;
        if (request.getProdutos() != null && !request.getProdutos().isEmpty()) {
            produtos = request.getProdutos().stream()
                .map(p -> new CompraAppService.ItemProduto(p.getProdutoId(), p.getQuantidade()))
                .collect(Collectors.toList());
        }

        // Cria a compra através do serviço (processa ingressos E produtos)
        Compra compraCompleta = compraAppService.criarCompra(
            new ClienteId(request.getClienteId().intValue()),
            ingressos,
            produtos
        );

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
            compraAppService.cancelarCompra(new CompraId(id));
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

        try {
            Compra compra = compraAppService.obterCompra(new CompraId(id));

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
        } catch (IllegalArgumentException e) {
            throw new RecursoNaoEncontradoException("Compra", Long.valueOf(id));
        }
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
            Ingresso ingresso = compraAppService.buscarIngressoPorQrCode(qrCode);

            // Buscar compra associada ao ingresso
            CompraId compraId = compraAppService.obterCompraIdPorIngresso(ingresso);
            Compra compra = compraAppService.obterCompra(compraId);

            // Mapeia ingresso com produtos da bomboniere
            IngressoDTO ingressoDTO = ingressoMapper.toDTOComProdutos(ingresso, compra.getCompraId().getId());

            Map<String, Object> response = Map.of(
                "ingresso", ingressoDTO,
                "compraId", compra.getCompraId().getId(),
                "clienteId", compra.getClienteId().getId()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            throw new RecursoNaoEncontradoException("Ingresso com QR Code " + qrCode + " não encontrado");
        } catch (Exception e) {
            log.error("Erro ao buscar ingresso por QR Code: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Erro interno ao buscar ingresso: " + e.getMessage()
            ));
        }
    }
}
