package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.servicos.FilmeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Filme
 * REFATORADO: Agora usa apenas FilmeService (sem acesso direto a repositórios)
 */
@RestController
@RequestMapping("/api/filmes")
@CrossOrigin(origins = "*")
public class FilmeController {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {
        this.filmeService = filmeService;
    }

    /**
     * Lista todos os filmes com filtros opcionais
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarFilmes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String busca) {
        try {
            List<FilmeService.FilmeDTO> filmes = filmeService.listarFilmes(status, busca);

            List<Map<String, Object>> response = filmes.stream()
                    .map(this::mapearFilmeParaMap)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lista filmes em cartaz
     */
    @GetMapping("/em-cartaz")
    public ResponseEntity<List<Map<String, Object>>> listarFilmesEmCartaz() {
        try {
            List<FilmeService.FilmeDTO> filmes = filmeService.listarFilmesEmCartaz();
            List<Map<String, Object>> response = filmes.stream()
                    .map(this::mapearFilmeParaMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Obtém um filme por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterFilme(@PathVariable Integer id) {
        try {
            FilmeService.FilmeDTO filme = filmeService.obterFilme(id);
            return ResponseEntity.ok(mapearFilmeParaMap(filme));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("erro", "Filme não encontrado"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao buscar filme: " + e.getMessage()));
        }
    }

    /**
     * Adiciona um novo filme
     */
    @PostMapping
    public ResponseEntity<?> adicionarFilme(@RequestBody FilmeRequest request) {
        try {
            // Validações
            if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Título é obrigatório"));
            }
            if (request.getDuracao() == null || request.getDuracao() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Duração deve ser positiva"));
            }

            FilmeService.FilmeDTO filmeSalvo = filmeService.adicionarFilme(
                    request.getTitulo(),
                    request.getSinopse(),
                    request.getClassificacaoEtaria(),
                    request.getDuracao(),
                    request.getImagemUrl()
            );
            return ResponseEntity.status(201).body(mapearFilmeParaMap(filmeSalvo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao adicionar filme: " + e.getMessage()));
        }
    }

    /**
     * Atualiza um filme existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFilme(@PathVariable Integer id,
                                           @RequestBody FilmeRequest request) {
        try {
            // Validações
            if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Título é obrigatório"));
            }
            if (request.getDuracao() == null || request.getDuracao() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Duração deve ser positiva"));
            }

            FilmeService.FilmeDTO filmeAtualizado = filmeService.atualizarFilme(
                    id,
                    request.getTitulo(),
                    request.getSinopse(),
                    request.getClassificacaoEtaria(),
                    request.getDuracao(),
                    request.getImagemUrl()
            );
            return ResponseEntity.ok(mapearFilmeParaMap(filmeAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao atualizar filme: " + e.getMessage()));
        }
    }

    /**
     * Remove um filme
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerFilme(@PathVariable Integer id) {
        try {
            filmeService.removerFilme(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao remover filme: " + e.getMessage()));
        }
    }

    /**
     * Verifica se um filme pode ser removido
     */
    @GetMapping("/{id}/pode-remover")
    public ResponseEntity<Map<String, Object>> podeRemover(@PathVariable Integer id) {
        try {
            FilmeService.VerificacaoRemocao verificacao = filmeService.verificarPodeRemover(id);

            Map<String, Object> response = new HashMap<>();
            response.put("podeRemover", verificacao.podeRemover());
            response.put("totalSessoes", verificacao.totalSessoes());

            if (!verificacao.podeRemover()) {
                response.put("mensagem", verificacao.mensagem());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao verificar filme: " + e.getMessage()));
        }
    }

    /**
     * Mapeia FilmeDTO para Map
     */
    private Map<String, Object> mapearFilmeParaMap(FilmeService.FilmeDTO filme) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", filme.id());
        dto.put("titulo", filme.titulo());
        dto.put("sinopse", filme.sinopse());
        dto.put("classificacaoEtaria", filme.classificacaoEtaria());
        dto.put("duracao", filme.duracao());
        dto.put("imagemUrl", filme.imagemUrl());
        dto.put("status", filme.status());
        return dto;
    }

    /**
     * Classe de Request para Filme
     */
    public static class FilmeRequest {
        private String titulo;
        private String sinopse;
        private String classificacaoEtaria;
        private Integer duracao;
        private String imagemUrl;
        private String status;

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getSinopse() {
            return sinopse;
        }

        public void setSinopse(String sinopse) {
            this.sinopse = sinopse;
        }

        public String getClassificacaoEtaria() {
            return classificacaoEtaria;
        }

        public void setClassificacaoEtaria(String classificacaoEtaria) {
            this.classificacaoEtaria = classificacaoEtaria;
        }

        public Integer getDuracao() {
            return duracao;
        }

        public void setDuracao(Integer duracao) {
            this.duracao = duracao;
        }

        public String getImagemUrl() {
            return imagemUrl;
        }

        public void setImagemUrl(String imagemUrl) {
            this.imagemUrl = imagemUrl;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
