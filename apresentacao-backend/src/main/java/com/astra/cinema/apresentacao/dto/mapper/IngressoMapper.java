package com.astra.cinema.apresentacao.dto.mapper;

import com.astra.cinema.apresentacao.dto.response.IngressoDTO;
import com.astra.cinema.dominio.compra.Ingresso;
import org.springframework.stereotype.Component;

@Component
public class IngressoMapper {

    public IngressoDTO toDTO(Ingresso ingresso) {
        if (ingresso == null) {
            return null;
        }

        return new IngressoDTO(
            ingresso.getIngressoId().getId(),
            ingresso.getQrCode(),
            ingresso.getSessaoId().getId(),
            ingresso.getAssentoId().getValor(),
            ingresso.getTipo().name(),
            ingresso.getStatus().name()
        );
    }
}
