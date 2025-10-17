package com.astra.controller;

import com.astra.dto.ClienteRequest;
import com.astra.dto.ClienteResponse;
import com.astra.model.Cliente;
import com.astra.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:3000")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody ClienteRequest request) {
        try {
            // Verifica se o email já existe
            if (clienteService.existeEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Email já cadastrado"));
            }

            // Verifica se o CPF já existe
            if (clienteService.existeCpf(request.getCpf())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("CPF já cadastrado"));
            }

            // Cria novo cliente
            Cliente cliente = new Cliente();
            cliente.setNome(request.getNome());
            cliente.setEmail(request.getEmail());
            cliente.setSenha(request.getSenha()); // TODO: Adicionar criptografia
            cliente.setCpf(request.getCpf());
            cliente.setTelefone(request.getTelefone());
            cliente.setDataNascimento(request.getDataNascimento());

            Cliente savedCliente = clienteService.salvar(cliente);

            ClienteResponse response = new ClienteResponse();
            response.setId(savedCliente.getId());
            response.setNome(savedCliente.getNome());
            response.setEmail(savedCliente.getEmail());
            response.setCpf(savedCliente.getCpf());
            response.setPontosFidelidade(savedCliente.getPontosFidelidade());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao cadastrar cliente: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCliente(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        
        if (cliente.isPresent()) {
            Cliente c = cliente.get();
            ClienteResponse response = new ClienteResponse();
            response.setId(c.getId());
            response.setNome(c.getNome());
            response.setEmail(c.getEmail());
            response.setCpf(c.getCpf());
            response.setPontosFidelidade(c.getPontosFidelidade());
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Cliente não encontrado"));
    }

    // Classe interna para mensagens de erro
    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
