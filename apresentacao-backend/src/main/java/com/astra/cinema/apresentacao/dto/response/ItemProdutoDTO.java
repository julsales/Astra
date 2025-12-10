package com.astra.cinema.apresentacao.dto.response;

public class ItemProdutoDTO {
    private Integer produtoId;
    private String nome;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;

    public ItemProdutoDTO() {
    }

    public ItemProdutoDTO(Integer produtoId, String nome, Integer quantidade, Double precoUnitario) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = quantidade * precoUnitario;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        if (precoUnitario != null) {
            this.subtotal = quantidade * precoUnitario;
        }
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
        if (quantidade != null) {
            this.subtotal = quantidade * precoUnitario;
        }
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
