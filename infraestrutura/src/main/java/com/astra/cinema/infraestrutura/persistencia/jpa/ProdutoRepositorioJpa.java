package com.astra.cinema.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;

/**
 * Implementação JPA do ProdutoRepositorio
 * Padrão: Adapter (adapta interface do domínio para JPA)
 */
@Component
public class ProdutoRepositorioJpa implements ProdutoRepositorio {

    @Autowired
    private ProdutoJpaRepository produtoJpaRepository;

    @Autowired
    private CinemaMapeador mapeador;

    @Override
    public void salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo");
        }

        // Verifica se o produto já existe (update) ou é novo (insert)
        ProdutoJpa produtoJpa;
        if (produtoJpaRepository.existsById(produto.getProdutoId().getId())) {
            // Update - usa o ID existente
            produtoJpa = new ProdutoJpa(
                produto.getProdutoId().getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque()
            );
        } else {
            // Insert - deixa o banco gerar o ID (SERIAL)
            produtoJpa = new ProdutoJpa(
                null,  // ID será gerado pelo banco
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque()
            );
        }

        produtoJpaRepository.save(produtoJpa);
    }

    @Override
    public void remover(ProdutoId produtoId) {
        if (produtoId == null) throw new IllegalArgumentException("O ID do produto não pode ser nulo");
        produtoJpaRepository.deleteById(produtoId.getId());
    }

    @Override
    public Produto obterPorId(ProdutoId produtoId) {
        if (produtoId == null) {
            throw new IllegalArgumentException("O ID do produto não pode ser nulo");
        }

        return produtoJpaRepository.findById(produtoId.getId())
                .map(mapeador::paraDominio)
                .orElse(null);
    }

    @Override
    public List<Produto> listarProdutos() {
        return produtoJpaRepository.findAll()
                .stream()
                .map(mapeador::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public double calcularValorInventario() {
        return produtoJpaRepository.findAll().stream()
                .mapToDouble(p -> {
                    double preco = p.getPreco() != null ? p.getPreco() : 0.0;
                    int estoque = p.getEstoque() != null ? p.getEstoque() : 0;
                    return preco * estoque;
                })
                .sum();
    }
}
