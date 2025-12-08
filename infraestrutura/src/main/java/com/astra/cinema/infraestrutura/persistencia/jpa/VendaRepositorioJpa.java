package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.StatusVenda;
import com.astra.cinema.dominio.bomboniere.Venda;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import com.astra.cinema.dominio.comum.PagamentoId;
import com.astra.cinema.dominio.comum.VendaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementação JPA do VendaRepositorio
 * Padrão: Adapter (adapta interface do domínio para JPA)
 * 
 * Nota: A tabela venda no banco armazena um produto por linha com ID auto-incrementado.
 * Para suportar múltiplos produtos por venda (modelo de domínio),
 * criamos múltiplas linhas e usamos o ID da primeira linha como vendaId de referência.
 */
@Component
public class VendaRepositorioJpa implements VendaRepositorio {

    @Autowired
    private VendaJpaRepository vendaJpaRepository;

    @Autowired
    private ProdutoJpaRepository produtoJpaRepository;

    @Autowired
    private CinemaMapeador mapeador;

    @Transactional
    public void salvar(Venda venda, Integer compraId) {
        if (venda == null) {
            throw new IllegalArgumentException("A venda não pode ser nula");
        }

        List<Produto> produtos = venda.getProdutos();
        if (produtos.isEmpty()) {
            throw new IllegalArgumentException("A venda deve ter pelo menos um produto");
        }

        Integer pagamentoId = venda.getPagamentoId() != null ? venda.getPagamentoId().getId() : null;
        String status = venda.getStatus().name();

        // Cria linhas para cada produto
        for (int i = 0; i < produtos.size(); i++) {
            Produto produto = produtos.get(i);
            VendaJpa vendaJpa = new VendaJpa();
            vendaJpa.setProdutoId(produto.getProdutoId().getId());
            vendaJpa.setQuantidade(1);
            vendaJpa.setPagamentoId(pagamentoId);
            vendaJpa.setStatus(status);
            vendaJpa.setCompraId(compraId); // Associa à compra
            vendaJpa.setCriadoEm(java.time.LocalDateTime.now());
            vendaJpaRepository.save(vendaJpa);
        }
    }

    @Override
    @Transactional
    public void salvar(Venda venda) {
        salvar(venda, null); // Chamada sem compraId (para compatibilidade)
    }

    @Override
    public Venda obterPorId(VendaId vendaId) {
        if (vendaId == null) {
            throw new IllegalArgumentException("O ID da venda não pode ser nulo");
        }

        // Busca a venda pelo ID (que corresponde ao ID do banco)
        VendaJpa vendaJpa = vendaJpaRepository.findById(vendaId.getId()).orElse(null);
        
        if (vendaJpa == null) {
            return null;
        }

        // Busca todas as vendas com o mesmo pagamentoId e status para agrupar produtos
        List<VendaJpa> vendasRelacionadas = vendaJpaRepository.findAll().stream()
            .filter(v -> v.getPagamentoId() != null && v.getPagamentoId().equals(vendaJpa.getPagamentoId()))
            .filter(v -> v.getStatus().equals(vendaJpa.getStatus()))
            .collect(Collectors.toList());

        // Se não houver pagamentoId, usa apenas a venda encontrada
        if (vendasRelacionadas.isEmpty()) {
            vendasRelacionadas = List.of(vendaJpa);
        }

        // Agrupa os produtos
        List<Produto> produtos = new ArrayList<>();
        Integer pagamentoId = null;
        String status = null;

        for (VendaJpa v : vendasRelacionadas) {
            ProdutoJpa produtoJpa = produtoJpaRepository.findById(v.getProdutoId()).orElse(null);
            if (produtoJpa != null) {
                Produto produto = mapeador.paraDominio(produtoJpa);
                for (int i = 0; i < v.getQuantidade(); i++) {
                    produtos.add(produto);
                }
            }
            if (v.getPagamentoId() != null) {
                pagamentoId = v.getPagamentoId();
            }
            if (v.getStatus() != null) {
                status = v.getStatus();
            }
        }

        if (produtos.isEmpty()) {
            return null;
        }

        PagamentoId pagId = pagamentoId != null ? new PagamentoId(pagamentoId) : null;
        StatusVenda statusVenda = status != null ? StatusVenda.valueOf(status) : StatusVenda.PENDENTE;

        return new Venda(vendaId, produtos, pagId, statusVenda);
    }

    @Override
    public List<Venda> listarVendasPorStatus(StatusVenda status) {
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }

        // Busca todas as vendas com o status especificado
        List<VendaJpa> vendasJpa = vendaJpaRepository.findByStatus(status.name());

        // Agrupa por pagamentoId (vendas com mesmo pagamentoId são da mesma venda)
        Map<Integer, List<VendaJpa>> vendasPorPagamento = vendasJpa.stream()
            .filter(v -> v.getPagamentoId() != null)
            .collect(Collectors.groupingBy(VendaJpa::getPagamentoId));

        List<Venda> vendas = new ArrayList<>();
        
        // Cria uma venda para cada grupo de pagamento
        for (Map.Entry<Integer, List<VendaJpa>> entry : vendasPorPagamento.entrySet()) {
            List<VendaJpa> grupo = entry.getValue();
            if (!grupo.isEmpty()) {
                VendaJpa primeira = grupo.get(0);
                VendaId vendaId = new VendaId(primeira.getId());
                Venda venda = obterPorId(vendaId);
                if (venda != null) {
                    vendas.add(venda);
                }
            }
        }

        // Adiciona vendas sem pagamentoId (pendentes)
        List<VendaJpa> vendasSemPagamento = vendasJpa.stream()
            .filter(v -> v.getPagamentoId() == null)
            .collect(Collectors.toList());
        
        for (VendaJpa v : vendasSemPagamento) {
            VendaId vendaId = new VendaId(v.getId());
            Venda venda = obterPorId(vendaId);
            if (venda != null && !vendas.contains(venda)) {
                vendas.add(venda);
            }
        }

        return vendas;
    }

    @Override
    public List<Venda> buscarPorCompra(com.astra.cinema.dominio.comum.CompraId compraId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O ID da compra não pode ser nulo");
        }

        // Busca todas as vendas associadas a esta compra
        List<VendaJpa> vendasJpa = vendaJpaRepository.findByCompraId(compraId.getId());

        // Agrupa por produto (múltiplas linhas com mesmo produto = quantidade > 1)
        Map<Integer, List<VendaJpa>> vendasPorProduto = vendasJpa.stream()
            .collect(Collectors.groupingBy(VendaJpa::getProdutoId));

        List<Venda> vendas = new ArrayList<>();

        // Para cada produto, cria uma Venda com a quantidade correta
        for (Map.Entry<Integer, List<VendaJpa>> entry : vendasPorProduto.entrySet()) {
            Integer produtoId = entry.getKey();
            List<VendaJpa> vendasDoProduto = entry.getValue();

            if (vendasDoProduto.isEmpty()) continue;

            VendaJpa primeira = vendasDoProduto.get(0);
            ProdutoJpa produtoJpa = produtoJpaRepository.findById(produtoId).orElse(null);

            if (produtoJpa == null) continue;

            Produto produto = mapeador.paraDominio(produtoJpa);

            // Quantidade total = soma das quantidades de todas as linhas
            int quantidadeTotal = vendasDoProduto.stream()
                .mapToInt(VendaJpa::getQuantidade)
                .sum();

            // Cria lista de produtos com a quantidade correta
            List<Produto> produtos = new ArrayList<>();
            for (int i = 0; i < quantidadeTotal; i++) {
                produtos.add(produto);
            }

            PagamentoId pagId = primeira.getPagamentoId() != null
                ? new PagamentoId(primeira.getPagamentoId())
                : null;
            StatusVenda status = StatusVenda.valueOf(primeira.getStatus());

            Venda venda = new Venda(
                new VendaId(primeira.getId()),
                produtos,
                pagId,
                status
            );

            vendas.add(venda);
        }

        return vendas;
    }
}

