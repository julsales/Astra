package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.compra.*;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.interface_adapters.dto.*;
import com.astra.cinema.interface_adapters.mapper.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Controller desabilitado temporariamente - funcionalidade não implementada nesta entrega
 * Para habilitar, remova este comentário e o comentário da anotação @RestController abaixo
 */

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Compra
 */
// @RestController -- DESABILITADO TEMPORARIAMENTE
// @RequestMapping("/api/compras")
public class CompraController {

    private final IniciarCompraUseCase iniciarCompraUseCase;
    private final ConfirmarCompraUseCase confirmarCompraUseCase;
    private final CancelarCompraUseCase cancelarCompraUseCase;

    public CompraController(IniciarCompraUseCase iniciarCompraUseCase,
                           ConfirmarCompraUseCase confirmarCompraUseCase,
                           CancelarCompraUseCase cancelarCompraUseCase) {
        this.iniciarCompraUseCase = iniciarCompraUseCase;
        this.confirmarCompraUseCase = confirmarCompraUseCase;
        this.cancelarCompraUseCase = cancelarCompraUseCase;
    }

    @PostMapping
    public ResponseEntity<CompraDTO> iniciarCompra(@RequestBody IniciarCompraRequest request) {
        List<Ingresso> ingressos = request.getIngressos().stream()
            .map(IngressoMapper::toDomain)
            .collect(Collectors.toList());
        
        Compra compra = iniciarCompraUseCase.executar(
            new ClienteId(request.getClienteId()),
            ingressos
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(CompraMapper.toDTO(compra));
    }

    @PutMapping("/{compraId}/confirmar")
    public ResponseEntity<Void> confirmarCompra(
            @PathVariable Integer compraId,
            @RequestBody ConfirmarCompraRequest request) {
        
        confirmarCompraUseCase.executar(
            new CompraId(compraId),
            new PagamentoId(request.getPagamentoId())
        );
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{compraId}")
    public ResponseEntity<Void> cancelarCompra(@PathVariable Integer compraId) {
        cancelarCompraUseCase.executar(new CompraId(compraId));
        return ResponseEntity.noContent().build();
    }

    // Classes internas para requests
    public static class IniciarCompraRequest {
        private Integer clienteId;
        private List<IngressoDTO> ingressos;

        public Integer getClienteId() {
            return clienteId;
        }

        public void setClienteId(Integer clienteId) {
            this.clienteId = clienteId;
        }

        public List<IngressoDTO> getIngressos() {
            return ingressos;
        }

        public void setIngressos(List<IngressoDTO> ingressos) {
            this.ingressos = ingressos;
        }
    }

    public static class ConfirmarCompraRequest {
        private Integer pagamentoId;

        public Integer getPagamentoId() {
            return pagamentoId;
        }

        public void setPagamentoId(Integer pagamentoId) {
            this.pagamentoId = pagamentoId;
        }
    }
}
