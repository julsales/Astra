package com.astra.cinema.interface_adapters.mapper;

import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.interface_adapters.dto.FilmeDTO;

/**
 * Mapper para converter entre entidades de Filme e DTOs
 */
public class FilmeMapper {

    public static FilmeDTO toDTO(Filme filme) {
        if (filme == null) return null;
        
        return new FilmeDTO(
            filme.getFilmeId().getId(),
            filme.getTitulo(),
            filme.getSinopse(),
            filme.getClassificacaoEtaria(),
            filme.getDuracao(),
            filme.getStatus().name(),
            filme.getImagemUrl()
        );
    }

    public static Filme toDomain(FilmeDTO dto) {
        if (dto == null) return null;
        
        return new Filme(
            new FilmeId(dto.getFilmeId()),
            dto.getTitulo(),
            dto.getSinopse(),
            dto.getClassificacaoEtaria(),
            dto.getDuracao(),
            dto.getImagemUrl(),
            StatusFilme.valueOf(dto.getStatus())
        );
    }
}
