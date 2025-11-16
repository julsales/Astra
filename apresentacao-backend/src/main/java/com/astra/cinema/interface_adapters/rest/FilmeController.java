package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.filme.AdicionarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.AlterarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.RemoverFilmeUseCase;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operações com Filmes
 * 
 * Padrão: Facade (simplifica interações com o sistema)
 * Padrão: DTO (usa objetos de transferência de dados)
 */
@RestController
@RequestMapping("/api/filmes")
@CrossOrigin(origins = "*")
public class FilmeController {

    @Autowired
    private FilmeRepositorio filmeRepositorio;

    @Autowired
    private AdicionarFilmeUseCase adicionarFilmeUseCase;

    @Autowired
    private AlterarFilmeUseCase alterarFilmeUseCase;

    @Autowired
    private RemoverFilmeUseCase removerFilmeUseCase;

    /**
     * Lista todos os filmes em cartaz
     */
    @GetMapping("/em-cartaz")
    public ResponseEntity<List<FilmeDTO>> listarFilmesEmCartaz() {
        try {
            List<Filme> filmes = filmeRepositorio.listarFilmesEmCartaz();
            List<FilmeDTO> filmesDTO = filmes.stream()
                    .map(this::converterParaDTO)
                    .toList();
            
            return ResponseEntity.ok(filmesDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca um filme por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FilmeDTO> buscarPorId(@PathVariable Integer id) {
        try {
            Filme filme = filmeRepositorio.obterPorId(new FilmeId(id));
            
            if (filme == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(converterParaDTO(filme));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove um filme do catálogo (apenas gerentes)
     * 
     * @param id ID do filme
     * @param requestBody Corpo da requisição com dados do funcionário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removerFilme(@PathVariable Integer id) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Executa remoção
            FilmeId filmeId = new FilmeId(id);
            removerFilmeUseCase.executar(filmeId);

            response.put("mensagem", "Filme removido com sucesso");
            response.put("status", "sucesso");
            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (IllegalStateException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_argumento");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("mensagem", "Erro interno ao remover filme: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verifica se um filme pode ser removido
     */
    @GetMapping("/{id}/pode-remover")
    public ResponseEntity<Map<String, Boolean>> podeRemover(@PathVariable Integer id) {
        try {
            FilmeId filmeId = new FilmeId(id);
            boolean podeRemover = removerFilmeUseCase.podeRemover(filmeId);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("podeRemover", podeRemover);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adiciona um novo filme (apenas gerentes)
     */
    @PostMapping
    public ResponseEntity<?> adicionarFilme(@RequestBody AdicionarFilmeRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Adiciona filme
            Filme filme = adicionarFilmeUseCase.executar(
                request.titulo(),
                request.sinopse(),
                request.classificacaoEtaria(),
                request.duracao()
            );

            response.put("mensagem", "Filme adicionado com sucesso");
            response.put("filme", converterParaDTO(filme));
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
     * Altera um filme existente (apenas gerentes)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> alterarFilme(
            @PathVariable Integer id,
            @RequestBody AlterarFilmeRequestDTO request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Altera filme
            Filme filme = alterarFilmeUseCase.executar(
                new FilmeId(id),
                request.titulo(),
                request.sinopse(),
                request.classificacaoEtaria(),
                request.duracao() != null ? request.duracao() : 0
            );

            response.put("mensagem", "Filme alterado com sucesso");
            response.put("filme", converterParaDTO(filme));
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
     * DTO para resposta de Filme
     */
    public record FilmeDTO(
        Integer id,
        String titulo,
        String sinopse,
        String classificacaoEtaria,
        Integer duracao,
        String status
    ) {}

    /**
     * DTO para requisição com dados do funcionário
     */
    public record FuncionarioRequestDTO(
        String nome,
        String cargo
    ) {}

    /**
     * DTO para adicionar filme
     */
    public record AdicionarFilmeRequestDTO(
        String titulo,
        String sinopse,
        String classificacaoEtaria,
        Integer duracao
    ) {}

    /**
     * DTO para alterar filme
     */
    public record AlterarFilmeRequestDTO(
        String titulo,
        String sinopse,
        String classificacaoEtaria,
        Integer duracao
    ) {}

    // ==================== Conversões ====================

    private FilmeDTO converterParaDTO(Filme filme) {
        return new FilmeDTO(
            filme.getFilmeId().getId(),
            filme.getTitulo(),
            filme.getSinopse(),
            filme.getClassificacaoEtaria(),
            filme.getDuracao(),
            filme.getStatus().toString()
        );
    }
}
