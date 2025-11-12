package com.astra.cinema.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;

/**
 * Entidade JPA para Produto
 * Representa um produto da bomboniere no banco de dados
 */
@Entity
@Table(name = "produto")
public class ProdutoJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "preco", nullable = false)
    private Double preco;
    
    @Column(name = "estoque", nullable = false)
    private Integer estoque;

    // Construtor padrão (obrigatório para JPA)
    public ProdutoJpa() {
    }

    // Construtor com parâmetros
    public ProdutoJpa(Integer id, String nome, Double preco, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }
}
