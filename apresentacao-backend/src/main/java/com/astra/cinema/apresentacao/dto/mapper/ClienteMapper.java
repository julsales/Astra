package com.astra.cinema.apresentacao.dto.mapper;

import com.astra.cinema.apresentacao.dto.response.ClienteDTO;
import com.astra.cinema.dominio.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    /**
     * Mapeia Usuario (Cliente) para DTO
     */
    public ClienteDTO toDTO(Usuario usuario) {
        return ClienteDTO.builder()
                .id(usuario.getId() != null ? Long.valueOf(usuario.getId().getValor()) : null)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .tipo("CLIENTE")
                .build();
    }

    /**
     * Mapeia Usuario (Cliente) para DTO com contagem de compras
     */
    public ClienteDTO toDTO(Usuario usuario, Integer totalCompras) {
        ClienteDTO dto = toDTO(usuario);
        dto.setTotalCompras(totalCompras);
        return dto;
    }
}
