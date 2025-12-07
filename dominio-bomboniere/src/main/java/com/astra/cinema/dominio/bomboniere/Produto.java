package com.astra.cinema.dominio.bomboniere;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNegativo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.ProdutoId;

public class Produto implements Cloneable {
    private final ProdutoId produtoId;
    private String nome;
    private double preco;
    private int estoque;

    public Produto(ProdutoId produtoId, String nome, double preco, int estoque) {
        this.produtoId = exigirNaoNulo(produtoId, "O id do produto não pode ser nulo");
        this.nome = exigirTexto(nome, "O nome não pode ser nulo ou vazio");
        this.preco = exigirNaoNegativo(preco, "O preço não pode ser negativo");
        this.estoque = exigirNaoNegativo(estoque, "O estoque não pode ser negativo");
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
        exigirPositivo(quantidade, "A quantidade deve ser positiva");
        exigirEstado(estoque >= quantidade, "Estoque insuficiente");
        this.estoque -= quantidade;
    }

    public void adicionarEstoque(int quantidade) {
        exigirPositivo(quantidade, "A quantidade deve ser positiva");
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
