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

    public CompraController(IniciarCompraUseCase iniciarCompraUseCase,
                           CancelarCompraUseCase cancelarCompraUseCase,
                           CompraRepositorio compraRepositorio,
                           IngressoMapper ingressoMapper) {
        this.iniciarCompraUseCase = iniciarCompraUseCase;
        this.cancelarCompraUseCase = cancelarCompraUseCase;
        this.compraRepositorio = compraRepositorio;
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

        // Salva a compra (gera QR Codes automaticamente)
        Compra compra = iniciarCompraUseCase.executar(
            new ClienteId(request.getClienteId().intValue()),
            ingressos
        );

        // Busca a compra salva para obter os QR Codes gerados
        Compra compraCompleta = compraRepositorio.obterPorId(compra.getCompraId());
        if (compraCompleta == null) {
            throw new RecursoNaoEncontradoException("Compra", Long.valueOf(compra.getCompraId().getId()));
        }

        // Mapeia ingressos com QR Codes
        List<IngressoDTO> ingressosDTO = compraCompleta.getIngressos().stream()
            .map(ingressoMapper::toDTO)
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

        cancelarCompraUseCase.executar(new CompraId(id));

        log.info("Compra {} cancelada com sucesso", id);
        return ResponseEntity.ok(Map.of(
            "mensagem", "Compra cancelada com sucesso",
            "compraId", id
        ));
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

        // Mapeia ingressos usando IngressoMapper
        List<IngressoDTO> ingressosDTO = compra.getIngressos().stream()
            .map(ingressoMapper::toDTO)
            .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
            "id", compra.getCompraId().getId(),
            "clienteId", compra.getClienteId().getId(),
            "status", compra.getStatus().name(),
            "ingressos", ingressosDTO
        );

        return ResponseEntity.ok(response);
    }
}

