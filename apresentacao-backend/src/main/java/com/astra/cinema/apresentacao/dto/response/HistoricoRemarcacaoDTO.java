package com.astra.cinema.apresentacao.dto.response;

import java.time.LocalDateTime;

public class HistoricoRemarcacaoDTO {
    private SessaoSimplificadaDTO sessaoOriginal;
    private String assentoOriginal;
    private LocalDateTime dataRemarcacao;
    private String motivo;

    public HistoricoRemarcacaoDTO() {
    }

    public HistoricoRemarcacaoDTO(SessaoSimplificadaDTO sessaoOriginal, String assentoOriginal, 
                                  LocalDateTime dataRemarcacao, String motivo) {
        this.sessaoOriginal = sessaoOriginal;
        this.assentoOriginal = assentoOriginal;
        this.dataRemarcacao = dataRemarcacao;
        this.motivo = motivo;
    }

    public SessaoSimplificadaDTO getSessaoOriginal() {
        return sessaoOriginal;
    }

    public void setSessaoOriginal(SessaoSimplificadaDTO sessaoOriginal) {
        this.sessaoOriginal = sessaoOriginal;
    }

    public String getAssentoOriginal() {
        return assentoOriginal;
    }

    public void setAssentoOriginal(String assentoOriginal) {
        this.assentoOriginal = assentoOriginal;
    }

    public LocalDateTime getDataRemarcacao() {
        return dataRemarcacao;
    }

    public void setDataRemarcacao(LocalDateTime dataRemarcacao) {
        this.dataRemarcacao = dataRemarcacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
