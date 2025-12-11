package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.servicos.BomboniereService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller REST para operações da Bomboniere
 * REFATORADO: Agora usa apenas BomboniereService (sem acesso direto a repositórios)
 */
@RestController
@RequestMapping("/api/funcionario/bomboniere")
@CrossOrigin(origins = "*")
public class BomboniereController {

    private final BomboniereService bomboniereService;

    public BomboniereController(BomboniereService bomboniereService) {
        this.bomboniereService = bomboniereService;
    }

    @GetMapping("/produtos")
    public ResponseEntity<?> listarProdutos() {
        try {
            List<BomboniereService.ProdutoDTO> produtos = bomboniereService.listarProdutos();

            List<Map<String, Object>> response = produtos.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.id());
                    map.put("nome", p.nome());
                    map.put("preco", p.preco());
                    map.put("estoque", p.estoque());
                    map.put("disponivel", p.disponivel());
                    map.put("categoria", p.categoria());
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
            List<BomboniereService.ItemVenda> itens = request.getItens().stream()
                .map(item -> new BomboniereService.ItemVenda(item.getProdutoId(), item.getQuantidade()))
                .collect(Collectors.toList());

            BomboniereService.ResultadoVenda resultado = bomboniereService.realizarVenda(itens);

            return ResponseEntity.ok(Map.of(
                "sucesso", resultado.sucesso(),
                "valorTotal", resultado.valorTotal(),
                "mensagem", resultado.mensagem()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/cancelar")
    public ResponseEntity<?> cancelarVenda(@RequestBody CancelarRequest request) {
        try {
            BomboniereService.ResultadoCancelamento resultado =
                bomboniereService.cancelarVenda(request.getVendaId());

            return ResponseEntity.ok(Map.of(
                "sucesso", resultado.sucesso(),
                "mensagem", resultado.mensagem()
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
}
