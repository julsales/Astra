package com.astra.cinema.apresentacao.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nome;
    private String email;
    private String tipo;
    private Integer totalCompras;
}
