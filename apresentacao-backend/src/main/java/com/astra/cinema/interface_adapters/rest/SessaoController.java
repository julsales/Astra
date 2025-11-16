package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.ModificarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemoverSessaoUseCase;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Controlador REST para operações de Sessão
 * 
 * Padrão: Facade (simplifica interações com o sistema)
 * Padrão: DTO (usa objetos de transferência de dados)
 */
@RestController
@RequestMapping("/api/sessoes")
@CrossOrigin(origins = "*")
public class SessaoController {

    @Autowired
    private CriarSessaoUseCase criarSessaoUseCase;

    @Autowired
    private ModificarSessaoUseCase modificarSessaoUseCase;

    @Autowired
    private RemoverSessaoUseCase removerSessaoUseCase;

    @Autowired
    private SessaoRepositorio sessaoRepositorio;

    /**
     * Cria uma nova sessão (apenas gerentes)
     * 
     * @param request Dados da sessão e do funcionário
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> criarSessao(@RequestBody CriarSessaoRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Converte horário String ISO para Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date horarioDate = sdf.parse(request.horario);

            // Cria a sessão (com capacidade padrão ou especificada)
            Sessao sessao = criarSessaoUseCase.executar(
                new FilmeId(request.filmeId),
                horarioDate,
                request.capacidadeSala != null ? request.capacidadeSala : 100
            );

            response.put("mensagem", "Sessão criada com sucesso");
            response.put("status", "sucesso");
            response.put("sessao", converterParaDTO(sessao));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

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

        } catch (java.text.ParseException e) {
            response.put("mensagem", "Formato de horário inválido. Use ISO 8601: " + e.getMessage());
            response.put("status", "erro_formato");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("mensagem", "Erro interno ao criar sessão: " + e.getMessage());
            response.put("status", "erro_interno");
            e.printStackTrace(); // Para debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Lista sessões de um filme
     */
    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<List<SessaoDTO>> listarPorFilme(@PathVariable Integer filmeId) {
        try {
            List<Sessao> sessoes = sessaoRepositorio.buscarPorFilme(new FilmeId(filmeId));
            List<SessaoDTO> sessoesDTO = sessoes.stream()
                    .map(this::converterParaDTO)
                    .toList();
            
            return ResponseEntity.ok(sessoesDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca uma sessão por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessaoDTO> buscarPorId(@PathVariable Integer id) {
        try {
            Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
            
            if (sessao == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(converterParaDTO(sessao));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verifica se uma sessão pode ser criada para um filme
     */
    @GetMapping("/filme/{filmeId}/pode-criar")
    public ResponseEntity<Map<String, Boolean>> podeCriar(@PathVariable Integer filmeId) {
        try {
            boolean podeCriar = criarSessaoUseCase.podeCriar(new FilmeId(filmeId));
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("podeCriar", podeCriar);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove (cancela) uma sessão (apenas gerentes)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removerSessao(@PathVariable Integer id) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Remove sessão
            removerSessaoUseCase.executar(new SessaoId(id));

            response.put("mensagem", "Sessão cancelada com sucesso");
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

        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Modifica o horário de uma sessão (apenas gerentes)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarSessao(
            @PathVariable Integer id,
            @RequestBody ModificarSessaoRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Modifica sessão
            Sessao sessao = modificarSessaoUseCase.executar(
                new SessaoId(id),
                request.novoHorario
            );

            response.put("mensagem", "Sessão modificada com sucesso");
            response.put("sessao", converterParaDTO(sessao));
            response.put("status", "sucesso");
            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (IllegalStateException | IllegalArgumentException e) {
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
     * DTO para requisição de criação de sessão
     */
    public static class CriarSessaoRequest {
        public Integer filmeId;
        public String horario;  // Recebe como String ISO do frontend
        public String sala;
        public Integer capacidadeSala;
        public FuncionarioDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    /**
     * DTO para funcionário dentro do request
     */
    public static class FuncionarioDTO {
        public String nome;
        public String cargo;
    }

    /**
     * DTO para modificar sessão
     */
    public static class ModificarSessaoRequest {
        public Date novoHorario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    /**
     * DTO para funcionário
     */
    public record FuncionarioRequestDTO(
        String nome,
        String cargo
    ) {}

    /**
     * DTO para resposta de Sessão
     */
    public record SessaoDTO(
        Integer id,
        Integer filmeId,
        Date horario,
        String status,
        Integer assentosDisponiveis,
        Integer totalAssentos
    ) {}

    // ==================== Conversões ====================

    private SessaoDTO converterParaDTO(Sessao sessao) {
        Map<AssentoId, Boolean> assentos = sessao.getMapaAssentosDisponiveis();
        long assentosDisponiveis = assentos.values().stream()
                .filter(disponivel -> disponivel)
                .count();
        
        return new SessaoDTO(
            sessao.getSessaoId().getId(),
            sessao.getFilmeId().getId(),
            sessao.getHorario(),
            sessao.getStatus().toString(),
            (int) assentosDisponiveis,
            assentos.size()
        );
    }
}
