package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

        ProdutoJpa produtoJpa = new ProdutoJpa(
            produto.getProdutoId().getId(),
            produto.getNome(),
            produto.getPreco(),
            produto.getEstoque()
        );

        produtoJpaRepository.save(produtoJpa);
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
}
