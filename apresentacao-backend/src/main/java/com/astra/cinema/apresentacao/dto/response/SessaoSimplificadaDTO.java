package com.astra.cinema.apresentacao.dto.response;

import java.time.LocalDateTime;

public class SessaoSimplificadaDTO {
    private Integer id;
    private String filme;
    private LocalDateTime horario;
    private String sala;

    public SessaoSimplificadaDTO() {
    }

    public SessaoSimplificadaDTO(Integer id, String filme, LocalDateTime horario, String sala) {
        this.id = id;
        this.filme = filme;
        this.horario = horario;
        this.sala = sala;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilme() {
        return filme;
    }

    public void setFilme(String filme) {
        this.filme = filme;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}
