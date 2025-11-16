package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.bomboniere.AdicionarProdutoUseCase;
import com.astra.cinema.aplicacao.bomboniere.ModificarProdutoUseCase;
import com.astra.cinema.aplicacao.bomboniere.RemoverProdutoUseCase;
import com.astra.cinema.aplicacao.usuario.GerenciarCinemaUseCase;
import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ProdutoId;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operações de Produtos (Bomboniere)
 * 
 * Padrão: Facade (simplifica interações com o sistema)
 * Padrão: DTO (usa objetos de transferência de dados)
 */
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private AdicionarProdutoUseCase adicionarProdutoUseCase;

    @Autowired
    private ModificarProdutoUseCase modificarProdutoUseCase;

    @Autowired
    private RemoverProdutoUseCase removerProdutoUseCase;

    @Autowired
    private GerenciarCinemaUseCase gerenciarCinemaUseCase;

    /**
     * Lista todos os produtos
     */
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarTodos() {
        try {
            List<Produto> produtos = produtoRepositorio.listarProdutos();
            List<ProdutoDTO> produtosDTO = produtos.stream()
                    .map(this::converterParaDTO)
                    .toList();
            
            return ResponseEntity.ok(produtosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca um produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Integer id) {
        try {
            Produto produto = produtoRepositorio.obterPorId(new ProdutoId(id));
            
            if (produto == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(converterParaDTO(produto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adiciona um novo produto (apenas gerentes)
     */
    @PostMapping
    public ResponseEntity<?> adicionarProduto(@RequestBody AdicionarProdutoRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Valida permissão
            Funcionario funcionario = new Funcionario(
                request.funcionario().nome(),
                Cargo.valueOf(request.funcionario().cargo())
            );
            gerenciarCinemaUseCase.validarPermissaoGerenciarProdutos(funcionario);

            // Adiciona produto
            Produto produto = adicionarProdutoUseCase.executar(
                request.nome(),
                request.preco(),
                request.estoqueInicial()
            );

            response.put("mensagem", "Produto adicionado com sucesso");
            response.put("produto", converterParaDTO(produto));
            response.put("status", "sucesso");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Modifica um produto existente (apenas gerentes)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarProduto(
            @PathVariable Integer id,
            @RequestBody ModificarProdutoRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Valida permissão
            Funcionario funcionario = new Funcionario(
                request.funcionario().nome(),
                Cargo.valueOf(request.funcionario().cargo())
            );
            gerenciarCinemaUseCase.validarPermissaoGerenciarProdutos(funcionario);

            // Modifica produto
            Produto produto = modificarProdutoUseCase.executar(
                new ProdutoId(id),
                request.nome(),
                request.preco() != null ? request.preco() : -1,
                request.estoque() != null ? request.estoque() : -1
            );

            response.put("mensagem", "Produto modificado com sucesso");
            response.put("produto", converterParaDTO(produto));
            response.put("status", "sucesso");
            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove um produto (apenas gerentes)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removerProduto(
            @PathVariable Integer id,
            @RequestBody FuncionarioRequestDTO request) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Valida permissão
            Funcionario funcionario = new Funcionario(
                request.nome(),
                Cargo.valueOf(request.cargo())
            );
            gerenciarCinemaUseCase.validarPermissaoGerenciarProdutos(funcionario);

            // Remove produto
            removerProdutoUseCase.executar(new ProdutoId(id));

            response.put("mensagem", "Produto removido com sucesso");
            response.put("status", "sucesso");
            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DTOs ====================

    /**
     * DTO para resposta de Produto
     */
    public record ProdutoDTO(
        Integer id,
        String nome,
        Double preco,
        Integer estoque
    ) {}

    /**
     * DTO para adicionar produto
     */
    public record AdicionarProdutoRequest(
        String nome,
        Double preco,
        Integer estoqueInicial,
        FuncionarioRequestDTO funcionario
    ) {}

    /**
     * DTO para modificar produto
     */
    public record ModificarProdutoRequest(
        String nome,
        Double preco,
        Integer estoque,
        FuncionarioRequestDTO funcionario
    ) {}

    /**
     * DTO para funcionário
     */
    public record FuncionarioRequestDTO(
        String nome,
        String cargo
    ) {}

    // ==================== Conversões ====================

    private ProdutoDTO converterParaDTO(Produto produto) {
        return new ProdutoDTO(
            produto.getProdutoId().getId(),
            produto.getNome(),
            produto.getPreco(),
            produto.getEstoque()
        );
    }
}
