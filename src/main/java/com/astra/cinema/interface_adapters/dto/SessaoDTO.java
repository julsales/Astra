package com.astra.cinema.interface_adapters.dto;

import java.util.Date;
import java.util.Map;

/**
 * DTO para transferência de dados de Sessão
 */
public class SessaoDTO {
    private Integer sessaoId;
    private Integer filmeId;
    private Date horario;
    private String status;
    private Map<String, Boolean> assentosDisponiveis;

    public SessaoDTO() {
    }

    public SessaoDTO(Integer sessaoId, Integer filmeId, Date horario, 
                     String status, Map<String, Boolean> assentosDisponiveis) {
        this.sessaoId = sessaoId;
        this.filmeId = filmeId;
        this.horario = horario;
        this.status = status;
        this.assentosDisponiveis = assentosDisponiveis;
    }

    public Integer getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Integer sessaoId) {
        this.sessaoId = sessaoId;
    }

    public Integer getFilmeId() {
        return filmeId;
    }

    public void setFilmeId(Integer filmeId) {
        this.filmeId = filmeId;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Boolean> getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public void setAssentosDisponiveis(Map<String, Boolean> assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }
}
