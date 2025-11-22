package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.usuario.funcionario.GerenciarFuncionariosUseCase;
import com.astra.cinema.dominio.comum.FuncionarioId;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final GerenciarFuncionariosUseCase gerenciarFuncionariosUseCase;

    public FuncionarioController(GerenciarFuncionariosUseCase gerenciarFuncionariosUseCase) {
        this.gerenciarFuncionariosUseCase = gerenciarFuncionariosUseCase;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String cargo) {
        try {
            Cargo cargoFiltro = null;
            if (cargo != null && !cargo.isBlank()) {
                try {
                    cargoFiltro = Cargo.valueOf(cargo.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            List<Funcionario> funcionarios = gerenciarFuncionariosUseCase.listar(busca, cargoFiltro);

            List<Map<String, Object>> response = funcionarios.stream()
                    .map(this::mapearFuncionario)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao listar funcionários: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalhar(@PathVariable Integer id) {
        try {
            Funcionario funcionario = gerenciarFuncionariosUseCase.detalhar(new FuncionarioId(id));
            return ResponseEntity.ok(mapearFuncionario(funcionario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao buscar funcionário: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarFuncionarioRequest request) {
        try {
            Cargo cargo = Cargo.valueOf(request.cargo.toUpperCase());
            Funcionario funcionario = gerenciarFuncionariosUseCase.criar(
                    request.nome,
                    cargo,
                    request.email,
                    request.senha
            );
            return ResponseEntity.ok(mapearFuncionario(funcionario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao criar funcionário: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @RequestBody AtualizarFuncionarioRequest request) {
        try {
            Cargo cargo = Cargo.valueOf(request.cargo.toUpperCase());
            Funcionario funcionario = gerenciarFuncionariosUseCase.atualizar(
                    new FuncionarioId(id),
                    request.nome,
                    cargo
            );
            return ResponseEntity.ok(mapearFuncionario(funcionario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao atualizar funcionário: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Integer id) {
        try {
            gerenciarFuncionariosUseCase.remover(new FuncionarioId(id));
            return ResponseEntity.ok(Map.of("mensagem", "Funcionário removido com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao remover funcionário: " + e.getMessage()));
        }
    }

    private Map<String, Object> mapearFuncionario(Funcionario funcionario) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", funcionario.getFuncionarioId() != null ? funcionario.getFuncionarioId().getValor() : null);
        map.put("nome", funcionario.getNome());
        map.put("cargo", funcionario.getCargo().name());
        return map;
    }

    public record CriarFuncionarioRequest(
            String nome,
            String email,
            String senha,
            String cargo
    ) {}

    public record AtualizarFuncionarioRequest(
            String nome,
            String cargo
    ) {}
}
