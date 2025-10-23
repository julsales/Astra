package com.astra.cinema.interface_adapters.dto;

import java.util.List;

/**
 * DTO para transferência de dados de Compra
 */
public class CompraDTO {
    private Integer compraId;
    private Integer clienteId;
    private List<IngressoDTO> ingressos;
    private Integer pagamentoId;
    private String status;

    public CompraDTO() {
    }

    public CompraDTO(Integer compraId, Integer clienteId, List<IngressoDTO> ingressos, 
                     Integer pagamentoId, String status) {
        this.compraId = compraId;
        this.clienteId = clienteId;
        this.ingressos = ingressos;
        this.pagamentoId = pagamentoId;
        this.status = status;
    }

    public Integer getCompraId() {
        return compraId;
    }

    public void setCompraId(Integer compraId) {
        this.compraId = compraId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public List<IngressoDTO> getIngressos() {
        return ingressos;
    }

    public void setIngressos(List<IngressoDTO> ingressos) {
        this.ingressos = ingressos;
    }

    public Integer getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Integer pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
