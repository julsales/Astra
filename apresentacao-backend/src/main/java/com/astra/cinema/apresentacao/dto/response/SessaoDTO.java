package com.astra.cinema.apresentacao.dto.response;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoDTO {
    private Integer id;
    private Integer filmeId;
    private String filmeTitulo;
    private Date horario;
    private String status;
    private Integer salaId;
    private String sala;
    private String salaNome; // Nome legível da sala (ex: "Sala 1", "Sala VIP")
    private Integer capacidade;
    private Integer assentosDisponiveis;
    private List<String> assentosOcupados;
    private Integer ingressosVendidos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssentoDTO {
        private String id;
        private Boolean disponivel;
    }
}
