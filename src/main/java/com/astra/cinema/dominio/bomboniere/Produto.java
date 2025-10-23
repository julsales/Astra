package com.astra.cinema.dominio.bomboniere;

import com.astra.cinema.dominio.comum.ProdutoId;

public class Produto implements Cloneable {
    private final ProdutoId produtoId;
    private String nome;
    private double preco;
    private int estoque;

    public Produto(ProdutoId produtoId, String nome, double preco, int estoque) {
        if (produtoId == null) {
            throw new IllegalArgumentException("O id do produto não pode ser nulo");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser nulo ou vazio");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }
        if (estoque < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo");
        }
        
        this.produtoId = produtoId;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public ProdutoId getProdutoId() {
        return produtoId;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void reduzirEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva");
        }
        if (estoque < quantidade) {
            throw new IllegalStateException("Estoque insuficiente");
        }
        this.estoque -= quantidade;
    }

    public void adicionarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva");
        }
        this.estoque += quantidade;
    }

    @Override
    public Produto clone() {
        try {
            return (Produto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
