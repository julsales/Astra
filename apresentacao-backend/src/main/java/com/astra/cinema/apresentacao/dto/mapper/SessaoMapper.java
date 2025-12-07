package com.astra.cinema.apresentacao.dto.mapper;

import com.astra.cinema.apresentacao.dto.response.SessaoDTO;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.sessao.Sessao;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SessaoMapper {

    /**
     * Mapeia Sessao completa com informações do filme
     */
    public SessaoDTO toDTO(Sessao sessao, Filme filme, Integer ingressosVendidos) {
        long assentosLivres = sessao.getMapaAssentosDisponiveis().values().stream()
                .filter(disponivel -> disponivel)
                .count();

        long assentosOcupados = sessao.getCapacidade() - assentosLivres;

        SessaoDTO.SessaoDTOBuilder builder = SessaoDTO.builder()
                .id(sessao.getSessaoId().getId())
                .filmeId(sessao.getFilmeId().getId())
                .salaId(sessao.getSalaId().getId())
                .sala("Sala " + sessao.getSalaId().getId()) // Temporário - será substituído por nome da sala
                .horario(sessao.getHorario())
                .capacidade(sessao.getCapacidade())
                .status(sessao.getStatus().name())
                .assentosDisponiveis((int) assentosLivres)
                .assentosOcupados(sessao.getMapaAssentosDisponiveis().entrySet().stream()
                        .filter(entry -> !entry.getValue())
                        .map(entry -> entry.getKey().getValor())
                        .collect(Collectors.toList()));

        if (filme != null) {
            builder.filmeTitulo(filme.getTitulo());
        }

        if (ingressosVendidos != null) {
            builder.ingressosVendidos(ingressosVendidos);
        }

        return builder.build();
    }

    /**
     * Mapeia Sessao básica (sem filme)
     */
    public SessaoDTO toDTO(Sessao sessao) {
        return toDTO(sessao, null, null);
    }
}
