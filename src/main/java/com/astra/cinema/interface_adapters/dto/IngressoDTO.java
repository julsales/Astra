package com.astra.cinema.interface_adapters.dto;

/**
 * DTO para transferência de dados de Ingresso
 */
public class IngressoDTO {
    private Integer ingressoId;
    private Integer sessaoId;
    private String assentoId;
    private String tipo;
    private String status;
    private String qrCode;

    public IngressoDTO() {
    }

    public IngressoDTO(Integer ingressoId, Integer sessaoId, String assentoId, 
                       String tipo, String status, String qrCode) {
        this.ingressoId = ingressoId;
        this.sessaoId = sessaoId;
        this.assentoId = assentoId;
        this.tipo = tipo;
        this.status = status;
        this.qrCode = qrCode;
    }

    public Integer getIngressoId() {
        return ingressoId;
    }

    public void setIngressoId(Integer ingressoId) {
        this.ingressoId = ingressoId;
    }

    public Integer getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Integer sessaoId) {
        this.sessaoId = sessaoId;
    }

    public String getAssentoId() {
        return assentoId;
    }

    public void setAssentoId(String assentoId) {
        this.assentoId = assentoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
