package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.interface_adapters.repository.ClientePainelRepository;
import com.astra.cinema.interface_adapters.repository.ClientePainelRepository.ClientePainelDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClientePainelRepository clientePainelRepository;

    public ClienteController(ClientePainelRepository clientePainelRepository) {
        this.clientePainelRepository = clientePainelRepository;
    }

    @GetMapping
    public ResponseEntity<List<ClientePainelDTO>> listar() {
        return ResponseEntity.ok(clientePainelRepository.listarClientes());
    }
}
