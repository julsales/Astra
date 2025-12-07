package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.usuario.AutenticarUsuarioUseCase;
import com.astra.cinema.aplicacao.usuario.RegistrarClienteUseCase;
import com.astra.cinema.apresentacao.dto.response.UsuarioAutenticadoDTO;
import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.ClienteRepositorio;
import com.astra.cinema.dominio.usuario.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final RegistrarClienteUseCase registrarClienteUseCase;
    private final ClienteRepositorio clienteRepositorio;

    public AuthController(
            AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            RegistrarClienteUseCase registrarClienteUseCase,
            ClienteRepositorio clienteRepositorio) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.registrarClienteUseCase = registrarClienteUseCase;
        this.clienteRepositorio = clienteRepositorio;
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
        
        // Se for um cliente, busca o clienteId
        Integer clienteId = null;
        if ("CLIENTE".equals(usuario.getTipo().name())) {
            try {
                Cliente cliente = clienteRepositorio.obterPorEmail(usuario.getEmail());
                if (cliente != null && cliente.getClienteId() != null) {
                    clienteId = cliente.getClienteId().getId();
                }
            } catch (Exception e) {
                // Se não encontrar o cliente, continua sem clienteId
                System.err.println("Erro ao buscar cliente por email: " + e.getMessage());
            }
        }
        
        UsuarioAutenticadoDTO dto = UsuarioAutenticadoDTO.builder()
                .id(usuario.getId().getValor())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .tipo(usuario.getTipo().name())
                .cargo(resultado.getCargo())
                .clienteId(clienteId)
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            RegistrarClienteUseCase.DadosRegistro dados = new RegistrarClienteUseCase.DadosRegistro(
                    request.getNome(),
                    request.getEmail(),
                    request.getSenha()
            );

            RegistrarClienteUseCase.ResultadoRegistro resultado = registrarClienteUseCase.executar(dados);

            Usuario usuario = resultado.getUsuario();
            Cliente cliente = resultado.getCliente();
            
            UsuarioAutenticadoDTO dto = UsuarioAutenticadoDTO.builder()
                    .id(usuario.getId().getValor())
                    .email(usuario.getEmail())
                    .nome(usuario.getNome())
                    .tipo(usuario.getTipo().name())
                    .cargo(null) // Clientes não têm cargo
                    .clienteId(cliente != null && cliente.getClienteId() != null ? cliente.getClienteId().getId() : null)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao registrar cliente: " + e.getMessage()));
        }
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

    public static class RegistroRequest {
        private String nome;
        private String email;
        private String senha;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

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
