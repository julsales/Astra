package com.astra.cinema.apresentacao.dto.response;

import java.time.LocalDateTime;

public class RemarcacaoDTO {
    private Integer id;
    private String qrCode;
    private String clienteNome;
    private LocalDateTime dataRemarcacao;
    private String motivoTecnico;
    private SessaoSimplificadaDTO sessaoOriginal;
    private SessaoSimplificadaDTO sessaoDestino;

    public RemarcacaoDTO() {
    }

    public RemarcacaoDTO(Integer id, String qrCode, String clienteNome, LocalDateTime dataRemarcacao,
                        String motivoTecnico, SessaoSimplificadaDTO sessaoOriginal, 
                        SessaoSimplificadaDTO sessaoDestino) {
        this.id = id;
        this.qrCode = qrCode;
        this.clienteNome = clienteNome;
        this.dataRemarcacao = dataRemarcacao;
        this.motivoTecnico = motivoTecnico;
        this.sessaoOriginal = sessaoOriginal;
        this.sessaoDestino = sessaoDestino;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public LocalDateTime getDataRemarcacao() {
        return dataRemarcacao;
    }

    public void setDataRemarcacao(LocalDateTime dataRemarcacao) {
        this.dataRemarcacao = dataRemarcacao;
    }

    public String getMotivoTecnico() {
        return motivoTecnico;
    }

    public void setMotivoTecnico(String motivoTecnico) {
        this.motivoTecnico = motivoTecnico;
    }

    public SessaoSimplificadaDTO getSessaoOriginal() {
        return sessaoOriginal;
    }

    public void setSessaoOriginal(SessaoSimplificadaDTO sessaoOriginal) {
        this.sessaoOriginal = sessaoOriginal;
    }

    public SessaoSimplificadaDTO getSessaoDestino() {
        return sessaoDestino;
    }

    public void setSessaoDestino(SessaoSimplificadaDTO sessaoDestino) {
        this.sessaoDestino = sessaoDestino;
    }
}
