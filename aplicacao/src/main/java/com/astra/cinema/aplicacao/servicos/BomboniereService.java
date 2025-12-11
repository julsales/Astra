package com.astra.cinema.aplicacao.servicos;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.bomboniere.StatusVenda;
import com.astra.cinema.dominio.bomboniere.Venda;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;
import com.astra.cinema.dominio.comum.VendaId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para operações da Bomboniere
 * Centraliza toda lógica de negócio relacionada a produtos e vendas
 */
public class BomboniereService {

    private final ProdutoRepositorio produtoRepositorio;
    private final VendaRepositorio vendaRepositorio;

    public BomboniereService(ProdutoRepositorio produtoRepositorio, VendaRepositorio vendaRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
        this.vendaRepositorio = vendaRepositorio;
    }

    /**
     * Lista todos os produtos com categoria categorizada
     */
    public List<ProdutoDTO> listarProdutos() {
        List<Produto> produtos = produtoRepositorio.listarProdutos();

        return produtos.stream()
            .map(p -> new ProdutoDTO(
                p.getProdutoId().getId(),
                p.getNome(),
                p.getPreco(),
                p.getEstoque(),
                p.getEstoque() > 0,
                categorizarProduto(p.getNome())
            ))
            .collect(Collectors.toList());
    }

    /**
     * Realiza uma venda de produtos
     * Calcula valor total, reduz estoque e cria registro de venda
     */
    public ResultadoVenda realizarVenda(List<ItemVenda> itens) {
        double valorTotal = 0.0;
        List<Produto> produtosVendidos = new ArrayList<>();

        for (ItemVenda item : itens) {
            Produto produto = produtoRepositorio.obterPorId(new ProdutoId(item.produtoId()));

            if (produto == null) {
                throw new RuntimeException("Produto não encontrado: " + item.produtoId());
            }

            // Reduzir estoque do produto
            produto.reduzirEstoque(item.quantidade());
            produtoRepositorio.salvar(produto);

            double subtotal = produto.getPreco() * item.quantidade();
            valorTotal += subtotal;

            // Adicionar produto à lista (repetir pela quantidade)
            for (int i = 0; i < item.quantidade(); i++) {
                produtosVendidos.add(produto);
            }
        }

        // Criar venda com ID temporário (será substituído pelo banco)
        Venda venda = new Venda(
            new VendaId(1),  // ID temporário válido, banco vai gerar o real
            produtosVendidos,
            null, // pagamentoId será definido depois
            StatusVenda.PENDENTE
        );

        // Salva no banco (JPA gera ID automaticamente)
        vendaRepositorio.salvar(venda);

        return new ResultadoVenda(true, valorTotal, "Venda realizada com sucesso");
    }

    /**
     * Cancela uma venda
     */
    public ResultadoCancelamento cancelarVenda(int vendaId) {
        // TODO: Implementar lógica de cancelamento real
        return new ResultadoCancelamento(true, "Venda cancelada com sucesso");
    }

    /**
     * Categoriza um produto baseado no seu nome
     */
    private String categorizarProduto(String nome) {
        if (nome == null) return "outros";

        String nomeLower = nome.toLowerCase();

        // Bebidas
        if (nomeLower.contains("refrigerante") || nomeLower.contains("coca") ||
            nomeLower.contains("pepsi") || nomeLower.contains("guaraná") ||
            nomeLower.contains("suco") || nomeLower.contains("água") ||
            nomeLower.contains("sprite") || nomeLower.contains("fanta")) {
            return "bebidas";
        }

        // Comidas
        if (nomeLower.contains("pipoca") || nomeLower.contains("nachos") ||
            nomeLower.contains("cachorro-quente") || nomeLower.contains("hot dog") ||
            nomeLower.contains("batata") || nomeLower.contains("doce")) {
            return "comidas";
        }

        // Combos
        if (nomeLower.contains("combo") || nomeLower.contains("kit")) {
            return "combos";
        }

        return "outros";
    }

    // Classes de resultado
    public record ProdutoDTO(
        int id,
        String nome,
        double preco,
        int estoque,
        boolean disponivel,
        String categoria
    ) {}

    public record ItemVenda(int produtoId, int quantidade) {}

    public record ResultadoVenda(boolean sucesso, double valorTotal, String mensagem) {}

    public record ResultadoCancelamento(boolean sucesso, String mensagem) {}
}
