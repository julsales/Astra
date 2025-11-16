package com.astra.cinema.interface_adapters.mapper;

import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.interface_adapters.dto.IngressoDTO;

/**
 * Mapper para converter entre entidades de Ingresso e DTOs
 */
public class IngressoMapper {

    public static IngressoDTO toDTO(Ingresso ingresso) {
        if (ingresso == null) return null;
        
        return new IngressoDTO(
            ingresso.getIngressoId().getId(),
            ingresso.getSessaoId().getId(),
            ingresso.getAssentoId().getValor(),
            ingresso.getTipo().name(),
            ingresso.getStatus().name(),
            ingresso.getQrCode()
        );
    }

    public static Ingresso toDomain(IngressoDTO dto) {
        if (dto == null) return null;
        
        return new Ingresso(
            new IngressoId(dto.getIngressoId()),
            new SessaoId(dto.getSessaoId()),
            new AssentoId(dto.getAssentoId()),
            TipoIngresso.valueOf(dto.getTipo()),
            StatusIngresso.valueOf(dto.getStatus()),
            dto.getQrCode()
        );
    }
}
