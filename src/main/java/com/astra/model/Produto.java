package com.astra.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    private CategoriaProduto categoria;

    @Column(nullable = false)
    private Integer estoque = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

    // Construtores
    public Produto() {
    }

    public Produto(String nome, BigDecimal preco, Integer estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    // Métodos de negócio
    public void reduzirEstoque(Integer quantidade) {
        if (quantidade > this.estoque) {
            throw new IllegalStateException("Estoque insuficiente");
        }
        this.estoque -= quantidade;
    }

    public void aumentarEstoque(Integer quantidade) {
        this.estoque += quantidade;
    }

    public boolean temEstoqueSuficiente(Integer quantidade) {
        return this.estoque >= quantidade;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public enum CategoriaProduto {
        PIPOCA,
        REFRIGERANTE,
        DOCE,
        COMBO,
        OUTROS
    }
}
