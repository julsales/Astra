package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.filme.AdicionarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.AlterarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.RemoverFilmeUseCase;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Filme
 * Padrão: Front Controller (Spring MVC)
 */
@RestController
@RequestMapping("/api/filmes")
@CrossOrigin(origins = "*")
public class FilmeController {

    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final AdicionarFilmeUseCase adicionarFilmeUseCase;
    private final AlterarFilmeUseCase alterarFilmeUseCase;
    private final RemoverFilmeUseCase removerFilmeUseCase;

    public FilmeController(FilmeRepositorio filmeRepositorio,
                          SessaoRepositorio sessaoRepositorio,
                          AdicionarFilmeUseCase adicionarFilmeUseCase,
                          AlterarFilmeUseCase alterarFilmeUseCase,
                          RemoverFilmeUseCase removerFilmeUseCase) {
        this.filmeRepositorio = filmeRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.adicionarFilmeUseCase = adicionarFilmeUseCase;
        this.alterarFilmeUseCase = alterarFilmeUseCase;
        this.removerFilmeUseCase = removerFilmeUseCase;
    }

    /**
     * Lista todos os filmes com filtros opcionais
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarFilmes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String busca) {
        try {
            List<Filme> filmes;

            if (status != null && !status.isEmpty()) {
                StatusFilme statusFilme = StatusFilme.valueOf(status.toUpperCase());
                if (statusFilme == StatusFilme.EM_CARTAZ) {
                    filmes = filmeRepositorio.listarFilmesEmCartaz();
                } else {
                    filmes = filmeRepositorio.listarTodos().stream()
                            .filter(f -> f.getStatus() == statusFilme)
                            .collect(Collectors.toList());
                }
            } else {
                filmes = filmeRepositorio.listarTodos();
            }

            // Filtro de busca por título
            if (busca != null && !busca.isEmpty()) {
                String buscaLower = busca.toLowerCase();
                filmes = filmes.stream()
                        .filter(f -> f.getTitulo().toLowerCase().contains(buscaLower))
                        .collect(Collectors.toList());
            }

            List<Map<String, Object>> response = filmes.stream()
                    .map(this::mapearFilmeParaDTO)
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
            List<Filme> filmes = filmeRepositorio.listarFilmesEmCartaz();
            List<Map<String, Object>> response = filmes.stream()
                    .map(this::mapearFilmeParaDTO)
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
            Filme filme = filmeRepositorio.obterPorId(new FilmeId(id));
            if (filme == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Filme não encontrado"));
            }
            return ResponseEntity.ok(mapearFilmeParaDTO(filme));
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

            // Executa o use case com parâmetros individuais
            Filme filmeSalvo = adicionarFilmeUseCase.executar(
                    request.getTitulo(),
                    request.getSinopse(),
                    request.getClassificacaoEtaria(),
                    request.getDuracao(),
                    request.getImagemUrl()
            );
            return ResponseEntity.status(201).body(mapearFilmeParaDTO(filmeSalvo));
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

            // Executa o use case com parâmetros individuais
            Filme filmeAtualizado = alterarFilmeUseCase.executar(
                    new FilmeId(id),
                    request.getTitulo(),
                    request.getSinopse(),
                    request.getClassificacaoEtaria(),
                    request.getDuracao(),
                    request.getImagemUrl()
            );
            return ResponseEntity.ok(mapearFilmeParaDTO(filmeAtualizado));
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
            removerFilmeUseCase.executar(new FilmeId(id));
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
            List<com.astra.cinema.dominio.sessao.Sessao> sessoes =
                sessaoRepositorio.buscarPorFilme(new FilmeId(id));

            boolean podeRemover = sessoes.isEmpty();
            Map<String, Object> response = new HashMap<>();
            response.put("podeRemover", podeRemover);
            response.put("totalSessoes", sessoes.size());

            if (!podeRemover) {
                response.put("mensagem", "O filme possui " + sessoes.size() + " sessão(ões) cadastrada(s)");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("erro", "Erro ao verificar filme: " + e.getMessage()));
        }
    }

    /**
     * Mapeia Filme para DTO (Map)
     */
    private Map<String, Object> mapearFilmeParaDTO(Filme filme) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", filme.getFilmeId().getId());
        dto.put("titulo", filme.getTitulo());
        dto.put("sinopse", filme.getSinopse());
        dto.put("classificacaoEtaria", filme.getClassificacaoEtaria());
        dto.put("duracao", filme.getDuracao());
        dto.put("imagemUrl", filme.getImagemUrl());
        dto.put("status", filme.getStatus().name());
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
