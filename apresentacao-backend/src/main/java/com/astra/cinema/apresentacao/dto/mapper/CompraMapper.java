package com.astra.cinema.apresentacao.dto.mapper;

import com.astra.cinema.dominio.compra.Compra;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CompraMapper {

    public Map<String, Object> toDTO(Compra compra) {
        if (compra == null) {
            return null;
        }

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", compra.getCompraId().getId());
        dto.put("compraId", compra.getCompraId().getId());
        dto.put("clienteId", compra.getClienteId().getId());
        dto.put("status", compra.getStatus().name());

        return dto;
    }
}
