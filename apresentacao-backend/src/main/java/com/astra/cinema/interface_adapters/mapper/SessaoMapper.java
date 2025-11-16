package com.astra.cinema.interface_adapters.mapper;

import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.interface_adapters.dto.SessaoDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper para converter entre entidades de Sessão e DTOs
 */
public class SessaoMapper {

    public static SessaoDTO toDTO(Sessao sessao) {
        if (sessao == null) return null;
        
        Map<String, Boolean> assentosDTO = sessao.getMapaAssentosDisponiveis()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                e -> e.getKey().getValor(),
                Map.Entry::getValue
            ));
        
        return new SessaoDTO(
            sessao.getSessaoId().getId(),
            sessao.getFilmeId().getId(),
            sessao.getHorario(),
            sessao.getStatus().name(),
            assentosDTO
        );
    }

    public static Sessao toDomain(SessaoDTO dto) {
        if (dto == null) return null;
        
        Map<AssentoId, Boolean> assentosDomain = new HashMap<>();
        if (dto.getAssentosDisponiveis() != null) {
            assentosDomain = dto.getAssentosDisponiveis()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    e -> new AssentoId(e.getKey()),
                    Map.Entry::getValue
                ));
        }
        
        return new Sessao(
            new SessaoId(dto.getSessaoId()),
            new FilmeId(dto.getFilmeId()),
            dto.getHorario(),
            StatusSessao.valueOf(dto.getStatus()),
            assentosDomain
        );
    }
}
