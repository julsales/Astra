package com.astra.cinema.apresentacao.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAutenticadoDTO {
    private Integer id;
    private String email;
    private String nome;
    private String tipo;
    private String cargo;
}
