package com.astra.controller;

import com.astra.dto.LoginRequest;
import com.astra.dto.LoginResponse;
import com.astra.model.Usuario;
import com.astra.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.autenticar(
            loginRequest.getEmail(), 
            loginRequest.getPassword()
        );
        
        if (usuario.isPresent()) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Login realizado com sucesso!");
            response.setUserType(usuario.get().getClass().getSimpleName().toLowerCase());
            response.setEmail(usuario.get().getEmail());
            
            return ResponseEntity.ok(response);
        } else {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Email ou senha inválidos!");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
