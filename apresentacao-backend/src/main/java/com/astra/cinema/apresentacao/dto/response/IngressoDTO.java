package com.astra.cinema.apresentacao.dto.response;

import java.util.List;

public class IngressoDTO {
    private Integer id;
    private String qrCode;
    private String codigo; // Alias para qrCode (compatibilidade)
    private Integer sessaoId;
    private String assento;
    private String tipo;
    private String status;
    private Boolean remarcado;
    private HistoricoRemarcacaoDTO historicoRemarcacao;
    private List<ItemProdutoDTO> produtosBomboniere;

    public IngressoDTO() {
    }

    public IngressoDTO(Integer id, String qrCode, Integer sessaoId, String assento, String tipo, String status) {
        this.id = id;
        this.qrCode = qrCode;
        this.codigo = qrCode; // Compatibilidade
        this.sessaoId = sessaoId;
        this.assento = assento;
        this.tipo = tipo;
        this.status = status;
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
        this.codigo = qrCode; // Mantém sincronizado
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
        this.qrCode = codigo; // Mantém sincronizado
    }

    public Integer getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Integer sessaoId) {
        this.sessaoId = sessaoId;
    }

    public String getAssento() {
        return assento;
    }

    public void setAssento(String assento) {
        this.assento = assento;
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

    public Boolean getRemarcado() {
        return remarcado;
    }

    public void setRemarcado(Boolean remarcado) {
        this.remarcado = remarcado;
    }

    public HistoricoRemarcacaoDTO getHistoricoRemarcacao() {
        return historicoRemarcacao;
    }

    public void setHistoricoRemarcacao(HistoricoRemarcacaoDTO historicoRemarcacao) {
        this.historicoRemarcacao = historicoRemarcacao;
    }

    public List<ItemProdutoDTO> getProdutosBomboniere() {
        return produtosBomboniere;
    }

    public void setProdutosBomboniere(List<ItemProdutoDTO> produtosBomboniere) {
        this.produtosBomboniere = produtosBomboniere;
    }
}
