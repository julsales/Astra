package com.astra.cinema.interface_adapters.dto;

/**
 * DTO para transferência de dados de Filme
 */
public class FilmeDTO {
    private Integer filmeId;
    private String titulo;
    private String sinopse;
    private String classificacaoEtaria;
    private Integer duracao;
    private String status;
    private String imagemUrl;

    public FilmeDTO() {
    }

    public FilmeDTO(Integer filmeId, String titulo, String sinopse, 
                    String classificacaoEtaria, Integer duracao, String status, String imagemUrl) {
        this.filmeId = filmeId;
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.classificacaoEtaria = classificacaoEtaria;
        this.duracao = duracao;
        this.status = status;
        this.imagemUrl = imagemUrl;
    }

    public Integer getFilmeId() {
        return filmeId;
    }

    public void setFilmeId(Integer filmeId) {
        this.filmeId = filmeId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getClassificacaoEtaria() {
        return classificacaoEtaria;
    }

    public void setClassificacaoEtaria(String classificacaoEtaria) {
        this.classificacaoEtaria = classificacaoEtaria;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}
