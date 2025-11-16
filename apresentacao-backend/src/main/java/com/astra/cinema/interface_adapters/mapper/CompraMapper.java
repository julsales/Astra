package com.astra.cinema.interface_adapters.mapper;

import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.interface_adapters.dto.*;
import java.util.stream.Collectors;

/**
 * Mapper para converter entre entidades de Compra e DTOs
 */
public class CompraMapper {

    public static CompraDTO toDTO(Compra compra) {
        if (compra == null) return null;
        
        return new CompraDTO(
            compra.getCompraId().getId(),
            compra.getClienteId().getId(),
            compra.getIngressos().stream()
                .map(IngressoMapper::toDTO)
                .collect(Collectors.toList()),
            compra.getPagamentoId() != null ? compra.getPagamentoId().getId() : null,
            compra.getStatus().name()
        );
    }

    public static Compra toDomain(CompraDTO dto) {
        if (dto == null) return null;
        
        return new Compra(
            new CompraId(dto.getCompraId()),
            new ClienteId(dto.getClienteId()),
            dto.getIngressos().stream()
                .map(IngressoMapper::toDomain)
                .collect(Collectors.toList()),
            dto.getPagamentoId() != null ? new PagamentoId(dto.getPagamentoId()) : null,
            StatusCompra.valueOf(dto.getStatus())
        );
    }
}
