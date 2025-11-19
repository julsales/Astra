package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.bomboniere.Venda;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/funcionario/bomboniere")
@CrossOrigin(origins = "*")
public class BomboniereController {

    private final ProdutoRepositorio produtoRepositorio;
    private final VendaRepositorio vendaRepositorio;

    public BomboniereController(ProdutoRepositorio produtoRepositorio,
                               VendaRepositorio vendaRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
        this.vendaRepositorio = vendaRepositorio;
    }

    @GetMapping("/produtos")
    public ResponseEntity<?> listarProdutos() {
        try {
            List<Produto> produtos = produtoRepositorio.listarProdutos();
            
            List<Map<String, Object>> response = produtos.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getProdutoId().getId());
                    map.put("nome", p.getNome());
                    map.put("preco", p.getPreco());
                    map.put("estoque", p.getEstoque());
                    map.put("disponivel", p.getEstoque() > 0);
                    map.put("categoria", categorizarProduto(p.getNome()));
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/venda")
    public ResponseEntity<?> realizarVenda(@RequestBody VendaRequest request) {
        try {
            // Calcular valor total e montar lista de produtos
            double valorTotal = 0.0;
            List<Produto> produtosVendidos = new ArrayList<>();
            
            for (ItemVenda item : request.getItens()) {
                Produto produto = produtoRepositorio.obterPorId(
                    new ProdutoId(item.getProdutoId())
                );

                if (produto == null) {
                    throw new RuntimeException("Produto não encontrado: " + item.getProdutoId());
                }

                // Reduzir estoque do produto
                produto.reduzirEstoque(item.getQuantidade());
                produtoRepositorio.salvar(produto);

                double subtotal = produto.getPreco() * item.getQuantidade();
                valorTotal += subtotal;

                // Adicionar produto à lista (repetir pela quantidade)
                for (int i = 0; i < item.getQuantidade(); i++) {
                    produtosVendidos.add(produto);
                }
            }
            
            // Criar venda com ID temporário (será substituído pelo banco)
            // O repositório JPA vai ignorar este ID e gerar um novo com SERIAL
            Venda venda = new Venda(
                new com.astra.cinema.dominio.comum.VendaId(1),  // ID temporário válido, banco vai gerar o real
                produtosVendidos,
                null, // pagamentoId será definido depois
                com.astra.cinema.dominio.bomboniere.StatusVenda.PENDENTE
            );

            // Salva no banco (JPA gera ID automaticamente)
            vendaRepositorio.salvar(venda);

            // Retorna sucesso (o ID real está no banco)
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "valorTotal", valorTotal,
                "mensagem", "Venda realizada com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/cancelar")
    public ResponseEntity<?> cancelarVenda(@RequestBody CancelarRequest request) {
        try {
            // Buscar venda e cancelar
            // Por enquanto apenas retorna sucesso
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Venda cancelada com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    // Classes de Request
    public static class VendaRequest {
        private List<ItemVenda> itens;

        public List<ItemVenda> getItens() {
            return itens;
        }

        public void setItens(List<ItemVenda> itens) {
            this.itens = itens;
        }
    }

    public static class ItemVenda {
        private int produtoId;
        private int quantidade;

        public int getProdutoId() {
            return produtoId;
        }

        public void setProdutoId(int produtoId) {
            this.produtoId = produtoId;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    }

    public static class CancelarRequest {
        private int vendaId;

        public int getVendaId() {
            return vendaId;
        }

        public void setVendaId(int vendaId) {
            this.vendaId = vendaId;
        }
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
}
