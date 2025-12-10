package com.astra.cinema.apresentacao.dto.mapper;

import com.astra.cinema.apresentacao.dto.response.IngressoDTO;
import com.astra.cinema.apresentacao.dto.response.ItemProdutoDTO;
import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpa;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IngressoMapper {

    private final VendaJpaRepository vendaJpaRepository;
    private final ProdutoRepositorio produtoRepositorio;

    public IngressoMapper(VendaJpaRepository vendaJpaRepository, ProdutoRepositorio produtoRepositorio) {
        this.vendaJpaRepository = vendaJpaRepository;
        this.produtoRepositorio = produtoRepositorio;
    }

    public IngressoDTO toDTO(Ingresso ingresso) {
        if (ingresso == null) {
            return null;
        }

        IngressoDTO dto = new IngressoDTO(
            ingresso.getIngressoId().getId(),
            ingresso.getQrCode(),
            ingresso.getSessaoId().getId(),
            ingresso.getAssentoId().getValor(),
            ingresso.getTipo().name(),
            ingresso.getStatus().name()
        );

        return dto;
    }

    public IngressoDTO toDTOComProdutos(Ingresso ingresso, Integer compraId) {
        IngressoDTO dto = toDTO(ingresso);
        
        if (dto != null && compraId != null) {
            List<ItemProdutoDTO> produtosBomboniere = buscarProdutosDaCompra(compraId);
            dto.setProdutosBomboniere(produtosBomboniere);
        }
        
        return dto;
    }

    private List<ItemProdutoDTO> buscarProdutosDaCompra(Integer compraId) {
        List<VendaJpa> vendas = vendaJpaRepository.findByCompraId(compraId);
        
        if (vendas == null || vendas.isEmpty()) {
            return new ArrayList<>();
        }

        // Agrupa por produto e soma as quantidades
        Map<Integer, Integer> produtosAgrupados = vendas.stream()
            .collect(Collectors.groupingBy(
                VendaJpa::getProdutoId,
                Collectors.summingInt(VendaJpa::getQuantidade)
            ));

        // Mapeia para DTOs
        return produtosAgrupados.entrySet().stream()
            .map(entry -> {
                Integer produtoId = entry.getKey();
                Integer quantidade = entry.getValue();
                
                Produto produto = produtoRepositorio.obterPorId(new ProdutoId(produtoId));
                if (produto != null) {
                    return new ItemProdutoDTO(
                        produtoId,
                        produto.getNome(),
                        quantidade,
                        produto.getPreco()
                    );
                }
                return null;
            })
            .filter(item -> item != null)
            .collect(Collectors.toList());
    }
}
