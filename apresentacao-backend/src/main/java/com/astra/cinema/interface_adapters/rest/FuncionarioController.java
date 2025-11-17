package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.usuario.GerenciarCinemaUseCase;
import com.astra.cinema.aplicacao.usuario.funcionario.GerenciarFuncionariosUseCase;
import com.astra.cinema.dominio.comum.FuncionarioId;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final GerenciarFuncionariosUseCase gerenciarFuncionariosUseCase;
    private final GerenciarCinemaUseCase gerenciarCinemaUseCase;
    private final FuncionarioEmailRepository funcionarioEmailRepository;

    public FuncionarioController(GerenciarFuncionariosUseCase gerenciarFuncionariosUseCase,
                                 GerenciarCinemaUseCase gerenciarCinemaUseCase,
                                 FuncionarioEmailRepository funcionarioEmailRepository) {
        this.gerenciarFuncionariosUseCase = gerenciarFuncionariosUseCase;
        this.gerenciarCinemaUseCase = gerenciarCinemaUseCase;
        this.funcionarioEmailRepository = funcionarioEmailRepository;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String cargo) {
        try {
            Cargo cargoFiltro = cargo != null && !cargo.isBlank() ? Cargo.valueOf(cargo.toUpperCase(Locale.ROOT)) : null;
            List<FuncionarioResponseDTO> resposta = gerenciarFuncionariosUseCase.listar(busca, cargoFiltro)
                    .stream()
                    .map(this::mapear)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(resposta);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> detalhar(@PathVariable Integer id) {
        try {
            Funcionario funcionario = gerenciarFuncionariosUseCase.detalhar(new FuncionarioId(id));
            return ResponseEntity.ok(mapear(funcionario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarFuncionarioRequest request) {
        try {
            validarPermissao(request.autorizacao());
            Funcionario novo = gerenciarFuncionariosUseCase.criar(request.nome(), converterCargo(request.cargo()), request.email(), request.senha());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensagem", "Funcionário criado com sucesso",
                            "funcionario", mapear(novo)));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensagem", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensagem", "Erro interno ao criar funcionário"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @RequestBody AtualizarFuncionarioRequest request) {
        try {
            validarPermissao(request.autorizacao());
            Funcionario atualizado = gerenciarFuncionariosUseCase.atualizar(
                    new FuncionarioId(id),
                    request.nome(),
                    converterCargo(request.cargo()));
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Funcionário atualizado com sucesso",
                    "funcionario", mapear(atualizado)));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensagem", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensagem", "Erro interno ao atualizar funcionário"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Integer id, @RequestBody(required = false) FuncionarioAutorizacaoDTO request) {
        try {
            validarPermissao(request);
            gerenciarFuncionariosUseCase.remover(new FuncionarioId(id));
            return ResponseEntity.ok(Map.of("mensagem", "Funcionário removido com sucesso"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensagem", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensagem", "Erro interno ao remover funcionário"));
        }
    }

    private void validarPermissao(FuncionarioAutorizacaoDTO autorizacaoDTO) {
        if (autorizacaoDTO == null) {
            throw new SecurityException("Dados do funcionário autenticado não informados");
        }
        Funcionario funcionario = new Funcionario(autorizacaoDTO.nome(), converterCargo(autorizacaoDTO.cargo()));
        gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "gerenciar funcionários");
    }

    private Cargo converterCargo(String cargo) {
        if (cargo == null || cargo.isBlank()) {
            throw new IllegalArgumentException("O cargo é obrigatório");
        }
        return Cargo.valueOf(cargo.trim().toUpperCase(Locale.ROOT));
    }

    private FuncionarioResponseDTO mapear(Funcionario funcionario) {
        Integer id = funcionario.getFuncionarioId() != null ? funcionario.getFuncionarioId().getValor() : null;
        String email = id != null ? funcionarioEmailRepository.buscarEmailPorFuncionarioId(id) : null;
        return new FuncionarioResponseDTO(id, funcionario.getNome(), email, funcionario.getCargo().name());
    }

    public record FuncionarioResponseDTO(Integer id, String nome, String email, String cargo) {}

    public record FuncionarioAutorizacaoDTO(String nome, String cargo) {}

    public record CriarFuncionarioRequest(String nome, String cargo, String email, String senha, FuncionarioAutorizacaoDTO autorizacao) {}

    public record AtualizarFuncionarioRequest(String nome, String cargo, FuncionarioAutorizacaoDTO autorizacao) {}
}
