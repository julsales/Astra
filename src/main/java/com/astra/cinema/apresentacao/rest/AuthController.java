package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.usuario.AutenticarUsuarioUseCase;
import com.astra.cinema.aplicacao.usuario.UsuarioDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<UsuarioDTO> usuario = autenticarUsuarioUseCase.executar(
                request.getEmail(),
                request.getSenha()
        );

        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse("Credenciais inválidas"));
        }

        return ResponseEntity.ok(usuario.get());
    }

    // Classes auxiliares
    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }

    public static class ErrorResponse {
        private String mensagem;

        public ErrorResponse(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }
    }
}
