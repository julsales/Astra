package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.usuario.AutenticarUsuarioUseCase;
import com.astra.cinema.apresentacao.dto.response.UsuarioAutenticadoDTO;
import com.astra.cinema.dominio.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        AutenticarUsuarioUseCase.ResultadoAutenticacao resultado = autenticarUsuarioUseCase.executar(
                request.getEmail(),
                request.getSenha()
        );

        if (resultado == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Credenciais inválidas"));
        }

        Usuario usuario = resultado.getUsuario();
        UsuarioAutenticadoDTO dto = UsuarioAutenticadoDTO.builder()
                .id(usuario.getId().getValor())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .tipo(usuario.getTipo().name())
                .cargo(resultado.getCargo())
                .build();

        return ResponseEntity.ok(dto);
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
