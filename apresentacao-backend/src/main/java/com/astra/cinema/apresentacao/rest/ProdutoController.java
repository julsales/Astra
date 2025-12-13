package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.bomboniere.*;
import com.astra.cinema.dominio.bomboniere.*;
import com.astra.cinema.dominio.comum.ProdutoId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Produto (Bomboniere)
 * Padrão: Front Controller (Spring MVC)
 */
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoRepositorio produtoRepositorio;
    private final AdicionarProdutoUseCase adicionarProdutoUseCase;
    private final ModificarProdutoUseCase modificarProdutoUseCase;
    private final RemoverProdutoUseCase removerProdutoUseCase;
    private final com.astra.cinema.aplicacao.bomboniere.EntradaEstoqueUseCase entradaEstoqueUseCase;
    private final com.astra.cinema.aplicacao.bomboniere.AjusteEstoqueUseCase ajusteEstoqueUseCase;

    public ProdutoController(ProdutoRepositorio produtoRepositorio,
                            AdicionarProdutoUseCase adicionarProdutoUseCase,
                            ModificarProdutoUseCase modificarProdutoUseCase,
                            RemoverProdutoUseCase removerProdutoUseCase,
                            com.astra.cinema.aplicacao.bomboniere.EntradaEstoqueUseCase entradaEstoqueUseCase,
                            com.astra.cinema.aplicacao.bomboniere.AjusteEstoqueUseCase ajusteEstoqueUseCase) {
        this.produtoRepositorio = produtoRepositorio;
        this.adicionarProdutoUseCase = adicionarProdutoUseCase;
        this.modificarProdutoUseCase = modificarProdutoUseCase;
        this.removerProdutoUseCase = removerProdutoUseCase;
        this.entradaEstoqueUseCase = entradaEstoqueUseCase;
        this.ajusteEstoqueUseCase = ajusteEstoqueUseCase;
    }

    /**
     * Registra entrada de estoque (adicionar quantidade ao estoque existente)
     */
    @PostMapping("/{id}/entrada")
    public ResponseEntity<?> registrarEntrada(@PathVariable Integer id, @RequestBody EntradaRequest request) {
        try {
            if (request == null || request.getQuantidade() == null || request.getQuantidade() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Quantidade inválida"));
            }

            var produto = entradaEstoqueUseCase.executar(new com.astra.cinema.dominio.comum.ProdutoId(id), request.getQuantidade());

            return ResponseEntity.ok(mapearProdutoParaDTO(produto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao registrar entrada: " + e.getMessage()));
        }
    }

    /**
     * Ajusta o estoque para um valor específico (definir estoque manualmente)
     */
    @PostMapping("/{id}/ajuste")
    public ResponseEntity<?> ajustarEstoque(@PathVariable Integer id, @RequestBody AjusteRequest request) {
        try {
            if (request == null || request.getNovoEstoque() == null || request.getNovoEstoque() < 0) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Novo estoque inválido"));
            }

            var produto = ajusteEstoqueUseCase.definirEstoque(new com.astra.cinema.dominio.comum.ProdutoId(id), request.getNovoEstoque());

            return ResponseEntity.ok(mapearProdutoParaDTO(produto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao ajustar estoque: " + e.getMessage()));
        }
    }

    /**
     * Lista todos os produtos
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarProdutos() {
        try {
            List<Produto> produtos = produtoRepositorio.listarProdutos();
            List<Map<String, Object>> response = produtos.stream()
                    .map(this::mapearProdutoParaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Obtém um produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterProduto(@PathVariable Integer id) {
        try {
            Produto produto = produtoRepositorio.obterPorId(new ProdutoId(id));
            if (produto == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Produto não encontrado"));
            }
            return ResponseEntity.ok(mapearProdutoParaDTO(produto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao buscar produto: " + e.getMessage()));
        }
    }

    /**
     * Adiciona um novo produto
     */
    @PostMapping
    public ResponseEntity<?> adicionarProduto(@RequestBody ProdutoRequest request) {
        try {
            // Validações
            if (request.getNome() == null || request.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Nome é obrigatório"));
            }
            if (request.getPreco() == null || request.getPreco() < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Preço deve ser positivo ou zero"));
            }
            if (request.getEstoque() == null || request.getEstoque() < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Estoque deve ser positivo ou zero"));
            }

            Produto produto = adicionarProdutoUseCase.executar(
                    request.getNome(),
                    request.getPreco(),
                    request.getEstoque()
            );

            return ResponseEntity.status(201).body(mapearProdutoParaDTO(produto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao adicionar produto: " + e.getMessage()));
        }
    }

    /**
     * Atualiza um produto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Integer id,
                                              @RequestBody ProdutoRequest request) {
        try {
            // Validações
            if (request.getNome() == null || request.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Nome é obrigatório"));
            }
            if (request.getPreco() == null || request.getPreco() < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Preço deve ser positivo ou zero"));
            }
            if (request.getEstoque() == null || request.getEstoque() < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Estoque deve ser positivo ou zero"));
            }

            Produto produto = modificarProdutoUseCase.executar(
                    new ProdutoId(id),
                    request.getNome(),
                    request.getPreco(),
                    request.getEstoque()
            );

            return ResponseEntity.ok(mapearProdutoParaDTO(produto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao atualizar produto: " + e.getMessage()));
        }
    }

    /**
     * Remove um produto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerProduto(@PathVariable Integer id) {
        try {
            removerProdutoUseCase.executar(new ProdutoId(id));
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao remover produto: " + e.getMessage()));
        }
    }

    /**
     * Mapeia Produto para DTO (Map)
     */
    private Map<String, Object> mapearProdutoParaDTO(Produto produto) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", produto.getProdutoId().getId());
        dto.put("nome", produto.getNome());
        dto.put("preco", produto.getPreco());
        dto.put("estoque", produto.getEstoque());
        dto.put("disponivel", produto.getEstoque() > 0);

        // Deriva categoria do nome do produto
        String categoria = inferirCategoria(produto.getNome());
        dto.put("categoria", categoria);

        return dto;
    }

    /**
     * Infere a categoria do produto baseado no nome
     */
    private String inferirCategoria(String nome) {
        if (nome == null) return "diversos";

        String nomeLower = nome.toLowerCase();

        if (nomeLower.contains("pipoca")) return "pipoca";
        if (nomeLower.contains("refri") || nomeLower.contains("refrigerante") ||
            nomeLower.contains("coca") || nomeLower.contains("guaraná") ||
            nomeLower.contains("sprite") || nomeLower.contains("fanta")) return "bebidas";
        if (nomeLower.contains("água") || nomeLower.contains("agua")) return "bebidas";
        if (nomeLower.contains("suco")) return "bebidas";
        if (nomeLower.contains("chocolate") || nomeLower.contains("bala") ||
            nomeLower.contains("chiclete") || nomeLower.contains("doce")) return "doces";
        if (nomeLower.contains("nachos") || nomeLower.contains("batata")) return "salgados";
        if (nomeLower.contains("combo")) return "combos";

        return "diversos";
    }

    /**
     * Classe de Request para Produto
     */
    public static class ProdutoRequest {
        private String nome;
        private Double preco;
        private Integer estoque;

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

    public static class EntradaRequest {
        private Integer quantidade;

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }
    }

    public static class AjusteRequest {
        private Integer novoEstoque;

        public Integer getNovoEstoque() {
            return novoEstoque;
        }

        public void setNovoEstoque(Integer novoEstoque) {
            this.novoEstoque = novoEstoque;
        }
    }
}
